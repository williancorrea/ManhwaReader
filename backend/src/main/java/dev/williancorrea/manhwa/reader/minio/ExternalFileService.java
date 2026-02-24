package dev.williancorrea.manhwa.reader.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;

@Service
public class ExternalFileService {

  private final MinioService minioService;

  public ExternalFileService(MinioService minioService) {
    this.minioService = minioService;
  }

  public String downloadWithAuthAndUpload(
      String fileUrl,
      String bearerToken,
      String originalFileName,
      String folderName
  ) throws IOException, InterruptedException {

    HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(fileUrl))
//        .header("Authorization", "Bearer " + bearerToken)
//        .header("User-Agent", "ManhwaReaderBot/1.0")
        .GET()
        .build();

    HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Falha no download: HTTP " + response.statusCode());
    }

    long contentLength = response.headers()
        .firstValueAsLong("Content-Length")
        .orElse(-1);

    return minioService.uploadStream(
        response.body(),
        originalFileName,
        contentLength,
        folderName
    );
  }
}