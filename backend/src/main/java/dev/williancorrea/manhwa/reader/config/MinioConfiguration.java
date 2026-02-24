package dev.williancorrea.manhwa.reader.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Configuration
public class MinioConfiguration {

  @Value("${minio.access.key}")
  private String accessKey;

  @Value("${minio.access.secret}")
  private String secretKey;

  @Value("${minio.url}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  @Bean
  @Primary
  public MinioClient minioClient() {
    return new MinioClient.Builder()
        .credentials(accessKey, secretKey)
        .endpoint(minioUrl)
        .build();
  }
}