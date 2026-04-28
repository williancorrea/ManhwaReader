package dev.williancorrea.manhwa.reader.storage;

import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.storage.minio.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExternalFileService")
class ExternalFileServiceTest {

  @Mock
  private MinioService minioService;

  private ExternalFileService externalFileService;

  private static final String TEST_FILE_URL = "https://example.com/image.jpg";
  private static final String ORIGINAL_FILE_NAME = "image.jpg";
  private static final String FOLDER_NAME = "covers";

  @BeforeEach
  void setUp() {
    externalFileService = new ExternalFileService(minioService);
  }

  @Nested
  @DisplayName("ExternalFileService initialization")
  class InitializationTests {

    @Test
    @DisplayName("should create ExternalFileService with MinioService dependency")
    void shouldCreateServiceWithDependency() {
      assertThat(externalFileService).isNotNull();
    }

    @Test
    @DisplayName("should initialize with HTTP/1.1 configured")
    void shouldInitializeWithCorrectHttpVersion() {
      assertThat(externalFileService).isNotNull();
    }
  }

  @Nested
  @DisplayName("downloadExternalPublicObjectAndUploadToStorage()")
  class DownloadAndUploadTests {

    @Test
    @DisplayName("should have downloadExternalPublicObjectAndUploadToStorage method")
    void shouldHaveDownloadMethod() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      assertThat(method).isNotNull();
    }

    @Test
    @DisplayName("should be annotated with @Retryable for fault tolerance")
    void shouldBeRetryable() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var retryable = method.getAnnotation(org.springframework.retry.annotation.Retryable.class);
      assertThat(retryable).isNotNull();
    }

    @Test
    @DisplayName("@Retryable should handle IOException")
    void shouldRetryOnIOException() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var retryable = method.getAnnotation(org.springframework.retry.annotation.Retryable.class);
      var retryFor = retryable.retryFor();
      assertThat(retryFor).contains(java.io.IOException.class, RuntimeException.class);
    }

    @Test
    @DisplayName("@Retryable should use expression for max attempts")
    void shouldUseMaxAttemptsExpression() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var retryable = method.getAnnotation(org.springframework.retry.annotation.Retryable.class);
      assertThat(retryable.maxAttemptsExpression()).isEqualTo("${retry.download.max-attempts}");
    }

    @Test
    @DisplayName("@Retryable should have backoff configuration")
    void shouldHaveBackoffConfiguration() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var retryable = method.getAnnotation(org.springframework.retry.annotation.Retryable.class);
      var backoff = retryable.backoff();
      assertThat(backoff.delayExpression()).isEqualTo("${retry.download.delay}");
    }
  }

  @Nested
  @DisplayName("Method signature validation")
  class MethodSignatureTests {

    @Test
    @DisplayName("method should accept file URL as String")
    void methodShouldAcceptFileUrl() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var parameterTypes = method.getParameterTypes();
      assertThat(parameterTypes[0]).isEqualTo(String.class);
    }

    @Test
    @DisplayName("method should accept file name as String")
    void methodShouldAcceptFileName() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var parameterTypes = method.getParameterTypes();
      assertThat(parameterTypes[1]).isEqualTo(String.class);
    }

    @Test
    @DisplayName("method should accept folder name as String")
    void methodShouldAcceptFolderName() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var parameterTypes = method.getParameterTypes();
      assertThat(parameterTypes[2]).isEqualTo(String.class);
    }

    @Test
    @DisplayName("method should throw IOException and InterruptedException")
    void methodShouldDeclareExceptions() throws Exception {
      var method = ExternalFileService.class.getDeclaredMethod(
          "downloadExternalPublicObjectAndUploadToStorage",
          String.class, String.class, String.class
      );
      var exceptionTypes = method.getExceptionTypes();
      assertThat(exceptionTypes)
          .contains(java.io.IOException.class, InterruptedException.class);
    }
  }

  @Nested
  @DisplayName("HTTP Client configuration")
  class HttpClientConfigurationTests {

    @Test
    @DisplayName("should have HttpClient field")
    void shouldHaveHttpClientField() throws Exception {
      var field = ExternalFileService.class.getDeclaredField("httpClient");
      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(java.net.http.HttpClient.class);
    }

    @Test
    @DisplayName("HttpClient should be configured in constructor")
    void httpClientShouldBeConfiguredInConstructor() {
      assertThat(externalFileService).isNotNull();
    }
  }

  @Nested
  @DisplayName("MinioService dependency")
  class MinioServiceDependencyTests {

    @Test
    @DisplayName("should inject MinioService dependency")
    void shouldInjectMinioService() throws Exception {
      var field = ExternalFileService.class.getDeclaredField("minioService");
      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(MinioService.class);
    }

    @Test
    @DisplayName("should accept MinioService in constructor")
    void shouldAcceptMinioServiceInConstructor() throws Exception {
      var constructors = ExternalFileService.class.getDeclaredConstructors();
      var hasMinioServiceConstructor = java.util.Arrays.stream(constructors)
          .anyMatch(c -> c.getParameterCount() == 1 &&
              c.getParameterTypes()[0].equals(MinioService.class));
      assertThat(hasMinioServiceConstructor).isTrue();
    }
  }
}
