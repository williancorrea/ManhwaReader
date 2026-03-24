package dev.williancorrea.manhwa.reader.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class ExternalFileService {

  private final MinioService minioService;

  public ExternalFileService(MinioService minioService) {
    this.minioService = minioService;
  }

  @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.download.max-attempts}", backoff = @Backoff(delayExpression = "${retry.download.delay}"))
  public String downloadWithAuthAndUpload(
      String fileUrl,
      String originalFileName,
      String folderName
  ) throws IOException, InterruptedException {

    HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(fileUrl))
        .GET()
        .build();

    HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Falha no download: HTTP " + response.statusCode());
    }

    return minioService.uploadStream(
        response.body(),
        originalFileName,
        response.headers(),
        folderName
    );
  }
}