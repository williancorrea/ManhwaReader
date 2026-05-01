package dev.williancorrea.manhwa.reader.storage.minio;


import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.storage.StorageInterface;
import dev.williancorrea.manhwa.reader.utils.RemoveAccentuationUtils;
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
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService implements StorageInterface {

  private static final int MULTIPART_THRESHOLD = 10 * 1024 * 1024;

  private final MinioClient minioClient;

  @Value("${minio.bucket.name}")
  private String bucketName;

  public MinioService(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public List<String> findAllObjects() {
    List<String> objects = new ArrayList<>();
    try {
      Iterable<Result<Item>> result = minioClient.listObjects(
          ListObjectsArgs.builder()
              .bucket(bucketName)
              .recursive(true)
              .build()
      );
      for (Result<Item> item : result) {
        objects.add(item.get().objectName());
      }
      return objects;
    } catch (Exception e) {
      throw new BusinessException("storage.error.list-objects", new Object[] {e.getMessage()}, e);
    }
  }

  @Override
  public InputStream findObjectByName(String fileName) {
    try {
      return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
    } catch (Exception e) {
      throw new BusinessException("storage.error.fetch-object", null, e);
    }
  }

  @Override
  public String findObjectByNamePresigned(String fileName) {
    try {
      return getPresignedUrl(fileName);
    } catch (Exception e) {
      throw new BusinessException("storage.error.fetch-object", null, e);
    }
  }

  @Override
  public void remove(String nameObject) {
    try {
      minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(nameObject).build());
    } catch (Exception e) {
      throw new BusinessException("storage.error.remove-object", new Object[] {e.getMessage()}, e);
    }
  }

  @Override
  public String getFileAsBase64(String fileName) {
    try (InputStream inputStream = findObjectByName(fileName)) {
      byte[] bytes = inputStream.readAllBytes();
      return Base64.getEncoder().encodeToString(bytes);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = renameFile(file.getOriginalFilename());
    try {
      ensureBucketExists(false);
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .build()
      );
      return getPresignedUrl(fileName);
    } catch (Exception e) {
      throw new BusinessException("storage.error.upload-file", new Object[] {e.getMessage()}, e);
    }
  }

  @Override
  public String uploadStream(InputStream stream, String originalFileName, HttpHeaders headers,
                             String originalFolderName) {
    String objectPath = normalizePath(originalFileName, originalFolderName).toLowerCase();
    try {
      ensureBucketExists(true);

      long fileSize = headers.firstValueAsLong("Content-Length").orElse(-1);
      String contentType = headers.firstValue("Content-Type").orElse("application/octet-stream");

      var objectArgs = PutObjectArgs.builder()
          .bucket(bucketName)
          .contentType(contentType)
          .object(objectPath);

      if (fileSize > 0) {
        objectArgs.stream(stream, fileSize, -1);
      } else {
        objectArgs.stream(stream, -1, MULTIPART_THRESHOLD);
      }
      minioClient.putObject(objectArgs.build());

      return getPresignedUrl(objectPath);
    } catch (Exception e) {
      throw new BusinessException("storage.error.upload-file", new Object[] {e.getMessage()}, e);
    }
  }

  private void ensureBucketExists(boolean makePublic) throws Exception {
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      if (makePublic) {
        makeBucketPublic();
      }
    }
  }

  private String getPresignedUrl(String objectPath) throws Exception {
    return minioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucketName)
            .object(objectPath)
            .build()
    );
  }

  private void makeBucketPublic() throws Exception {
    String policy = """
        {
          "Version":"2012-10-17",
          "Statement":[
            {
              "Effect":"Allow",
              "Principal":"*",
              "Action":["s3:GetObject"],
              "Resource":["arn:aws:s3:::%s/*"]
            }
          ]
        }
        """.formatted(bucketName);

    minioClient.setBucketPolicy(
        SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build()
    );
  }

  private String normalizePath(String originalFileName, String originalFolderName) {
    Objects.requireNonNull(originalFileName);
    Objects.requireNonNull(originalFolderName);

    String fileName = RemoveAccentuationUtils.normalize(originalFileName);

    StringBuilder folderName = new StringBuilder();
    for (String segment : originalFolderName.split("/")) {
      if (!folderName.isEmpty()) {
        folderName.append("/");
      }
      folderName.append(RemoveAccentuationUtils.normalize(segment));
    }

    return folderName.isEmpty() ? fileName : folderName.append("/").append(fileName).toString();
  }

  private String renameFile(String originalName) {
    Objects.requireNonNull(originalName);
    return RemoveAccentuationUtils.removeAccentuation(originalName.trim());
  }
}
