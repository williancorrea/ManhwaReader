package dev.williancorrea.manhwa.reader.minio;


import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import dev.williancorrea.manhwa.reader.exception.ObjectNotFoundException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService implements FileUploaderInterface {

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
      throw new ObjectNotFoundException("Error listing object: " + e.getMessage());
    }
  }

  @Override
  public InputStream findObjectByName(String fileName) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .build()
      );
    } catch (Exception e) {

      throw new ObjectNotFoundException("Error fetching object: ");
    }
  }

  @Override
  public String findObjectByNamePresigned(String fileName) {
    try {
      return this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
          .method(Method.GET)
          .bucket(bucketName)
          .object(fileName)
          .build());
    } catch (Exception e) {

      throw new ObjectNotFoundException("Error fetching object: ");
    }
  }

  @Override
  public void remove(String nameObject) {
    try {
      minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(nameObject).build());
    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
             | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
             | IllegalArgumentException | IOException e) {

      throw new ObjectNotFoundException("Error removing object: " + e.getMessage());
    }
  }

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = renameFile(file.getOriginalFilename());
    try {
      if (!this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
        this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucketName).build());
      }

      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .build()
      );
      return this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
          .method(Method.GET)
          .bucket(bucketName)
          .object(fileName)
          .build());

    } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException |
             InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException |
             IllegalArgumentException | IOException e) {

      throw new ObjectNotFoundException("An error occurred while uploading: " + e.getMessage());
    }
  }

  public String uploadStream(InputStream stream, String originalFileName, long fileSize, String originalFolderName) {

    String fileName = renameFile(originalFileName.replace(":", ""));
    String folderName = renameFile(originalFolderName.replace(":", ""));
    String objectPath = (folderName != null && !folderName.isBlank())
        ? folderName + "/" + fileName
        : fileName;

    try {
      if (!this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
        this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucketName).build());
      }

      var objectArgs = PutObjectArgs.builder()
          .bucket(bucketName)
          .object(objectPath.toUpperCase());
      if (fileSize > 0) {
        objectArgs.stream(stream, fileSize, -1); //Tamanho do arquivo valido
      } else {
        objectArgs.stream(stream, -1, 10 * 1024 * 1024);// Definindo um partSize válido (mínimo 5MB).
      }
      minioClient.putObject(objectArgs.build());

      return this.minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(objectPath.toUpperCase())
              .build()
      );

    } catch (Exception e) {
      throw new ObjectNotFoundException(
          "Error uploading stream: " + e.getMessage());
    }
  }

  private String renameFile(String originalName) {
    if (originalName != null) {
      return RemoveAccentuationUtils.removeAccentuation(originalName.trim());
    }
    return null;
  }
}