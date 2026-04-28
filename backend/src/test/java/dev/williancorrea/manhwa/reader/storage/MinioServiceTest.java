package dev.williancorrea.manhwa.reader.storage;

import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.storage.minio.MinioService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinioService")
class MinioServiceTest {

  @Mock
  private MinioClient minioClient;

  @InjectMocks
  private MinioService minioService;

  private static final String BUCKET_NAME = "test-bucket";
  private static final String TEST_FILE_NAME = "test-file.txt";
  private static final String TEST_FOLDER = "test-folder";
  private static final String PRESIGNED_URL = "http://minio:9000/presigned-url";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(minioService, "bucketName", BUCKET_NAME);
  }

  @Nested
  @DisplayName("findAllObjects()")
  class FindAllObjectsTests {

    @Test
    @DisplayName("should return all objects from bucket")
    void shouldReturnAllObjects() throws Exception {
      var item1 = mock(Item.class);
      var item2 = mock(Item.class);
      when(item1.objectName()).thenReturn("object1.txt");
      when(item2.objectName()).thenReturn("object2.txt");

      @SuppressWarnings("unchecked")
      var result1 = mock(Result.class);
      @SuppressWarnings("unchecked")
      var result2 = mock(Result.class);
      when(result1.get()).thenReturn(item1);
      when(result2.get()).thenReturn(item2);

      when(minioClient.listObjects(any(ListObjectsArgs.class)))
          .thenReturn(List.of(result1, result2));

      var result = minioService.findAllObjects();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains("object1.txt", "object2.txt");
      verify(minioClient).listObjects(any(ListObjectsArgs.class));
    }

    @Test
    @DisplayName("should return empty list when no objects exist")
    void shouldReturnEmptyList() throws Exception {
      when(minioClient.listObjects(any(ListObjectsArgs.class)))
          .thenReturn(List.of());

      var result = minioService.findAllObjects();

      assertThat(result).isEmpty();
      verify(minioClient).listObjects(any(ListObjectsArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException when listObjects fails")
    void shouldThrowBusinessExceptionOnListObjectsFailure() throws Exception {
      when(minioClient.listObjects(any(ListObjectsArgs.class)))
          .thenThrow(new RuntimeException("Connection error"));

      assertThatThrownBy(() -> minioService.findAllObjects())
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.list-objects");
      verify(minioClient).listObjects(any(ListObjectsArgs.class));
    }
  }

  @Nested
  @DisplayName("findObjectByName()")
  class FindObjectByNameTests {

    @Test
    @DisplayName("should throw BusinessException when object not found")
    void shouldThrowBusinessExceptionWhenObjectNotFound() throws Exception {
      doThrow(new IOException("Object not found"))
          .when(minioClient).getObject(any(GetObjectArgs.class));

      assertThatThrownBy(() -> minioService.findObjectByName(TEST_FILE_NAME))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.fetch-object");
      verify(minioClient).getObject(any(GetObjectArgs.class));
    }
  }

  @Nested
  @DisplayName("findObjectByNamePresigned()")
  class FindObjectByNamePresignedTests {

    @Test
    @DisplayName("should return presigned URL")
    void shouldReturnPresignedUrl() throws Exception {
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.findObjectByNamePresigned(TEST_FILE_NAME);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException when presigned URL generation fails")
    void shouldThrowBusinessExceptionOnPresignedUrlFailure() throws Exception {
      doThrow(new IOException("Failed to generate presigned URL"))
          .when(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));

      assertThatThrownBy(() -> minioService.findObjectByNamePresigned(TEST_FILE_NAME))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.fetch-object");
      verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }
  }

  @Nested
  @DisplayName("remove()")
  class RemoveTests {

    @Test
    @DisplayName("should remove object successfully")
    void shouldRemoveObjectSuccessfully() throws Exception {
      minioService.remove(TEST_FILE_NAME);

      verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException when object removal fails")
    void shouldThrowBusinessExceptionOnRemovalFailure() throws Exception {
      doThrow(new IOException("Failed to remove object"))
          .when(minioClient).removeObject(any(RemoveObjectArgs.class));

      assertThatThrownBy(() -> minioService.remove(TEST_FILE_NAME))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.remove-object");
      verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException on InvalidKeyException")
    void shouldThrowBusinessExceptionOnInvalidKeyException() throws Exception {
      doThrow(new InvalidKeyException("Invalid key"))
          .when(minioClient).removeObject(any(RemoveObjectArgs.class));

      assertThatThrownBy(() -> minioService.remove(TEST_FILE_NAME))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.remove-object");
    }

    @Test
    @DisplayName("should throw BusinessException on NoSuchAlgorithmException")
    void shouldThrowBusinessExceptionOnNoSuchAlgorithmException() throws Exception {
      doThrow(new NoSuchAlgorithmException("Algorithm not found"))
          .when(minioClient).removeObject(any(RemoveObjectArgs.class));

      assertThatThrownBy(() -> minioService.remove(TEST_FILE_NAME))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.remove-object");
    }
  }

  @Nested
  @DisplayName("getFileAsBase64()")
  class GetFileAsBase64Tests {

    @Test
    @DisplayName("should return null when file not found")
    void shouldReturnNullWhenFileNotFound() throws Exception {
      doThrow(new IOException("File not found"))
          .when(minioClient).getObject(any(GetObjectArgs.class));

      var result = minioService.getFileAsBase64(TEST_FILE_NAME);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return null when exception occurs during encoding")
    void shouldReturnNullOnException() throws Exception {
      doThrow(new RuntimeException("Encoding error"))
          .when(minioClient).getObject(any(GetObjectArgs.class));

      var result = minioService.getFileAsBase64(TEST_FILE_NAME);

      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("uploadFile()")
  class UploadFileTests {

    @Test
    @DisplayName("should upload file successfully when bucket exists")
    void shouldUploadFileSuccessfullyWhenBucketExists() throws Exception {
      var multipartFile = mockMultipartFile(TEST_FILE_NAME, "content");
      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadFile(multipartFile);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).bucketExists(any(BucketExistsArgs.class));
      verify(minioClient).putObject(any(PutObjectArgs.class));
      verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @DisplayName("should create bucket if it does not exist")
    void shouldCreateBucketIfNotExists() throws Exception {
      var multipartFile = mockMultipartFile(TEST_FILE_NAME, "content");
      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(false);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      minioService.uploadFile(multipartFile);

      verify(minioClient).bucketExists(any(BucketExistsArgs.class));
      verify(minioClient).makeBucket(any(MakeBucketArgs.class));
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException when upload fails")
    void shouldThrowBusinessExceptionOnUploadFailure() throws Exception {
      var multipartFile = mockMultipartFile(TEST_FILE_NAME, "content");
      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      doThrow(new IOException("Upload failed"))
          .when(minioClient).putObject(any(PutObjectArgs.class));

      assertThatThrownBy(() -> minioService.uploadFile(multipartFile))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.upload-file");
    }

    @Test
    @DisplayName("should handle file with accentuation in name")
    void shouldHandleFileWithAccentuation() throws Exception {
      var multipartFile = mockMultipartFile("arquivo_com_acentuação.txt", "content");
      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadFile(multipartFile);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should verify presigned URL is retrieved after successful upload")
    void shouldRetrievePresignedUrl() throws Exception {
      var multipartFile = mockMultipartFile(TEST_FILE_NAME, "content");
      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadFile(multipartFile);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }
  }

  @Nested
  @DisplayName("uploadStream()")
  class UploadStreamTests {

    @Test
    @DisplayName("should upload stream successfully when bucket exists")
    void shouldUploadStreamSuccessfullyWhenBucketExists() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", 7L);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadStream(inputStream, TEST_FILE_NAME, headers, TEST_FOLDER);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).bucketExists(any(BucketExistsArgs.class));
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should create bucket and set public policy if it does not exist")
    void shouldCreateBucketAndSetPublicPolicy() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", 7L);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(false);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      minioService.uploadStream(inputStream, TEST_FILE_NAME, headers, TEST_FOLDER);

      verify(minioClient).bucketExists(any(BucketExistsArgs.class));
      verify(minioClient).makeBucket(any(MakeBucketArgs.class));
      verify(minioClient).setBucketPolicy(any(SetBucketPolicyArgs.class));
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should handle stream with unknown content length")
    void shouldHandleStreamWithUnknownContentLength() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", null);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadStream(inputStream, TEST_FILE_NAME, headers, TEST_FOLDER);

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should normalize folder path with slashes")
    void shouldNormalizeFolderPathWithSlashes() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", 7L);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      minioService.uploadStream(inputStream, TEST_FILE_NAME, headers, "folder1/folder2");

      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should handle file with accentuation in folder and filename")
    void shouldHandleFileWithAccentuationInFolderAndFilename() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", 7L);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
          .thenReturn(PRESIGNED_URL);

      var result = minioService.uploadStream(inputStream, "arquivo_acentuado.txt", headers, "pasta_acentuada");

      assertThat(result).isEqualTo(PRESIGNED_URL);
      verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("should throw BusinessException when upload stream fails")
    void shouldThrowBusinessExceptionOnUploadStreamFailure() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());
      var headers = mockHttpHeaders("application/octet-stream", 7L);

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(true);
      doThrow(new IOException("Upload failed"))
          .when(minioClient).putObject(any(PutObjectArgs.class));

      assertThatThrownBy(() -> minioService.uploadStream(inputStream, TEST_FILE_NAME, headers, TEST_FOLDER))
          .isInstanceOf(BusinessException.class)
          .hasMessage("storage.error.upload-file");
    }

    @Test
    @DisplayName("should throw BusinessException when bucket creation fails")
    void shouldThrowBusinessExceptionOnBucketCreationFailure() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(false);
      doThrow(new IOException("Bucket creation failed"))
          .when(minioClient).makeBucket(any(MakeBucketArgs.class));

      assertThatThrownBy(() -> minioService.uploadStream(inputStream, TEST_FILE_NAME, null, TEST_FOLDER))
          .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("should throw BusinessException when setting bucket policy fails")
    void shouldThrowBusinessExceptionOnSetBucketPolicyFailure() throws Exception {
      var inputStream = new ByteArrayInputStream("content".getBytes());

      when(minioClient.bucketExists(any(BucketExistsArgs.class)))
          .thenReturn(false);
      doThrow(new IOException("Set bucket policy failed"))
          .when(minioClient).setBucketPolicy(any(SetBucketPolicyArgs.class));

      assertThatThrownBy(() -> minioService.uploadStream(inputStream, TEST_FILE_NAME, null, TEST_FOLDER))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  @DisplayName("uploadStream() with null parameters")
  class UploadStreamNullParameterTests {

    @Test
    @DisplayName("should throw NullPointerException when fileName is null")
    void shouldThrowNullPointerExceptionWhenFileNameIsNull() {
      var inputStream = new ByteArrayInputStream("content".getBytes());

      assertThatThrownBy(() -> minioService.uploadStream(inputStream, null, null, TEST_FOLDER))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should throw NullPointerException when folderName is null")
    void shouldThrowNullPointerExceptionWhenFolderNameIsNull() {
      var inputStream = new ByteArrayInputStream("content".getBytes());

      assertThatThrownBy(() -> minioService.uploadStream(inputStream, TEST_FILE_NAME, null, null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  private MultipartFile mockMultipartFile(String fileName, String content) throws IOException {
    var multipartFile = mock(MultipartFile.class);
    when(multipartFile.getOriginalFilename()).thenReturn(fileName);
    when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes()));
    when(multipartFile.getSize()).thenReturn((long) content.length());
    return multipartFile;
  }

  private HttpHeaders mockHttpHeaders(String contentType, Long contentLength) {
    var headers = mock(HttpHeaders.class);
    when(headers.firstValue("Content-Type")).thenReturn(Optional.of(contentType));
    when(headers.firstValueAsLong("Content-Length")).thenReturn(
        contentLength != null ? OptionalLong.of(contentLength) : OptionalLong.empty()
    );
    return headers;
  }
}
