package dev.williancorrea.manhwa.reader.minio;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

public class FileTransferService {

  public static void downloadWithAuthAndUpload(
      String fileUrl,
      String bucket,
      String objectName,
      String endpoint,
      String accessKey,
      String secretKey
  ) throws Exception {

    // ======= CONFIG AUTH =======

    String bearerToken = "SEU_TOKEN_AQUI";

    String username = "usuario";
    String password = "senha";
    String basicAuth = Base64.getEncoder()
        .encodeToString((username + ":" + password).getBytes());

    // ======= HTTP CLIENT =======

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(fileUrl))
//        .header("Authorization", "Bearer " + bearerToken) // Bearer
//        .header("Authorization", "Basic " + basicAuth)    // ou Basic (use só um na vida real)
        .header("User-Agent", "Java HttpClient Bot/1.0")
        .GET()
        .build();

    HttpResponse<InputStream> response =
        client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Falha no download: HTTP " + response.statusCode());
    }

    // ======= MINIO CLIENT =======

    MinioClient minioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();

    // ======= UPLOAD STREAMING =======

    minioClient.putObject(
        PutObjectArgs.builder()
            .bucket(bucket)
            .object(objectName)
            .stream(response.body(), -1, 10 * 1024 * 1024)
            .contentType("application/octet-stream")
            .build()
    );
  }
}