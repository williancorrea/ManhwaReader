package dev.williancorrea.manhwa.reader.storage;

import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.storage.minio.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.annotation.Retryable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExternalFileService")
class ExternalFileServiceTest {

  @Mock
  private MinioService minioService;

  @Mock
  private HttpClient httpClient;

  private ExternalFileService externalFileService;

  private static final String TEST_FILE_URL = "https://example.com/image.jpg";
  private static final String ORIGINAL_FILE_NAME = "image.jpg";
  private static final String FOLDER_NAME = "covers";

  @BeforeEach
  void setUp() {
    externalFileService = new ExternalFileService(minioService);
    ReflectionTestUtils.setField(externalFileService, "httpClient", httpClient);
  }

  @Nested
  @DisplayName("downloadExternalPublicObjectAndUploadToStorage()")
  class DownloadAndUploadTests {

    @Test
    @DisplayName("should download and delegate upload to MinioService when HTTP 200")
    void shouldDownloadAndUploadSuccessfully() throws Exception {
      var responseBody = new ByteArrayInputStream("image-content".getBytes());
      var mockHeaders = mock(HttpHeaders.class);
      var mockResponse = mockHttpResponse(200, responseBody, mockHeaders);
      doReturn(mockResponse).when(httpClient).send(any(HttpRequest.class), any());

      externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, ORIGINAL_FILE_NAME, FOLDER_NAME
      );

      verify(httpClient).send(any(HttpRequest.class), any());
      verify(minioService).uploadStream(responseBody, ORIGINAL_FILE_NAME, mockHeaders, FOLDER_NAME);
    }

    @Test
    @DisplayName("should pass correct fileName and folderName to MinioService")
    void shouldPassCorrectParametersToMinioService() throws Exception {
      var responseBody = new ByteArrayInputStream("content".getBytes());
      var mockHeaders = mock(HttpHeaders.class);
      var mockResponse = mockHttpResponse(200, responseBody, mockHeaders);
      doReturn(mockResponse).when(httpClient).send(any(HttpRequest.class), any());

      externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, "custom-file.jpg", "my-folder"
      );

      verify(minioService).uploadStream(responseBody, "custom-file.jpg", mockHeaders, "my-folder");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 500, 503})
    @DisplayName("should throw BusinessException when HTTP response status is not 200")
    void shouldThrowBusinessExceptionWhenResponseIsNotOk(int statusCode) throws Exception {
      var mockResponse = mockHttpResponse(statusCode, InputStream.nullInputStream(), mock(HttpHeaders.class));
      doReturn(mockResponse).when(httpClient).send(any(HttpRequest.class), any());

      assertThatThrownBy(() -> externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, ORIGINAL_FILE_NAME, FOLDER_NAME
      )).isInstanceOf(BusinessException.class)
        .hasMessage("storage.error.download-failed");
    }

    @Test
    @DisplayName("should propagate IOException thrown by HttpClient")
    void shouldPropagateIOExceptionFromHttpClient() throws Exception {
      doThrow(new IOException("Connection refused")).when(httpClient).send(any(HttpRequest.class), any());

      assertThatThrownBy(() -> externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, ORIGINAL_FILE_NAME, FOLDER_NAME
      )).isInstanceOf(IOException.class)
        .hasMessageContaining("Connection refused");
    }

    @Test
    @DisplayName("should propagate InterruptedException thrown by HttpClient")
    void shouldPropagateInterruptedExceptionFromHttpClient() throws Exception {
      doThrow(new InterruptedException("Thread interrupted")).when(httpClient).send(any(HttpRequest.class), any());

      assertThatThrownBy(() -> externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, ORIGINAL_FILE_NAME, FOLDER_NAME
      )).isInstanceOf(InterruptedException.class);
    }

    @Test
    @DisplayName("should propagate BusinessException thrown by MinioService during upload")
    void shouldPropagateBusinessExceptionFromMinioService() throws Exception {
      var responseBody = new ByteArrayInputStream("image-content".getBytes());
      var mockHeaders = mock(HttpHeaders.class);
      var mockResponse = mockHttpResponse(200, responseBody, mockHeaders);
      doReturn(mockResponse).when(httpClient).send(any(HttpRequest.class), any());
      doThrow(new BusinessException("storage.error.upload-file", null))
          .when(minioService).uploadStream(any(), any(), any(), any());

      assertThatThrownBy(() -> externalFileService.downloadExternalPublicObjectAndUploadToStorage(
          TEST_FILE_URL, ORIGINAL_FILE_NAME, FOLDER_NAME
      )).isInstanceOf(BusinessException.class)
        .hasMessage("storage.error.upload-file");
    }
  }

  @Nested
  @DisplayName("@Retryable annotation")
  class RetryableAnnotationTests {

    @Test
    @DisplayName("should annotate method with @Retryable")
    void shouldAnnotateMethodWithRetryable() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      assertThat(method.getAnnotation(Retryable.class)).isNotNull();
    }

    @Test
    @DisplayName("@Retryable should retry on both IOException and RuntimeException")
    void shouldRetryOnIOExceptionAndRuntimeException() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var retryFor = method.getAnnotation(Retryable.class).retryFor();
      assertThat(retryFor).contains(IOException.class, RuntimeException.class);
    }

    @Test
    @DisplayName("@Retryable should resolve max attempts from property expression")
    void shouldUsePropertyExpressionForMaxAttempts() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      assertThat(method.getAnnotation(Retryable.class).maxAttemptsExpression())
          .isEqualTo("${retry.download.max-attempts}");
    }

    @Test
    @DisplayName("@Retryable should resolve backoff delay from property expression")
    void shouldUsePropertyExpressionForBackoffDelay() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      assertThat(method.getAnnotation(Retryable.class).backoff().delayExpression())
          .isEqualTo("${retry.download.delay}");
    }
  }

  @Nested
  @DisplayName("HttpClient configuration")
  class HttpClientConfigurationTests {

    @Test
    @DisplayName("should configure HttpClient with HTTP/1.1 to avoid stream concurrency limits")
    void shouldConfigureHttpClientWithHttp11() throws Exception {
      var freshService = new ExternalFileService(minioService);
      var httpClientField = ExternalFileService.class.getDeclaredField("httpClient");
      httpClientField.setAccessible(true);
      var client = (HttpClient) httpClientField.get(freshService);
      assertThat(client.version()).isEqualTo(HttpClient.Version.HTTP_1_1);
    }

    @Test
    @DisplayName("should configure HttpClient to follow redirects normally")
    void shouldConfigureHttpClientToFollowRedirects() throws Exception {
      var freshService = new ExternalFileService(minioService);
      var httpClientField = ExternalFileService.class.getDeclaredField("httpClient");
      httpClientField.setAccessible(true);
      var client = (HttpClient) httpClientField.get(freshService);
      assertThat(client.followRedirects()).isEqualTo(HttpClient.Redirect.NORMAL);
    }
  }

  @SuppressWarnings("unchecked")
  private HttpResponse<InputStream> mockHttpResponse(int statusCode, InputStream body, HttpHeaders headers) {
    var response = (HttpResponse<InputStream>) mock(HttpResponse.class);
    when(response.statusCode()).thenReturn(statusCode);
    if (statusCode == 200) {
      when(response.body()).thenReturn(body);
      when(response.headers()).thenReturn(headers);
    }
    return response;
  }
}
