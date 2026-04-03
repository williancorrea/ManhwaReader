package dev.williancorrea.manhwa.reader.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import dev.williancorrea.manhwa.reader.storage.minio.MinioService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class ExternalFileService {

  private final MinioService minioService;
  private final HttpClient httpClient;

  public ExternalFileService(MinioService minioService) {
    this.minioService = minioService;
    this.httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
  }

  @Retryable(retryFor = {RuntimeException.class, IOException.class}, maxAttemptsExpression = "${retry.download.max-attempts}", backoff = @Backoff(delayExpression = "${retry.download.delay}"))
  public void downloadExternalPublicObjectAndUploadToStorage(
      String fileUrl,
      String originalFileName,
      String folderName
  ) throws IOException, InterruptedException {

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(fileUrl))
        .GET()
        .build();

    HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Falha no download: HTTP " + response.statusCode());
    }

    minioService.uploadStream(
        response.body(),
        originalFileName,
        response.headers(),
        folderName
    );
  }
}