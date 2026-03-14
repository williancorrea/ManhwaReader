package dev.williancorrea.manhwa.reader.scraper.mangotoons.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.MangoCrypto;

public class MangoClient1 {

  private static final String BASE =
      "https://mangotoons.com";

  private final HttpClient httpClient;

  private final String token;

  public MangoClient1(String token) {
    this.httpClient = HttpClient.newHttpClient();
    this.token = token;

  }

  public String getRecentes(String formato_id) throws Exception {

    String url = BASE + "/api/obras/recentes?pagina=1&limite=20&formato_id=" + formato_id;
//    String url = BASE + "/api/obras/recentes?pagina=1&limite=20";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header("Accept", "*/*")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    String encryptedPayload = response.body();
    return MangoCrypto.decrypt(encryptedPayload);
  }

  public String getFormatos() throws Exception {

    String url = BASE + "/api/filtros/formatos";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header("Accept", "*/*")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    String encryptedPayload = response.body();
    return MangoCrypto.decrypt(encryptedPayload);
  }

  public String getStatus() throws Exception {

    String url = BASE + "/api/filtros/status";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header("Accept", "*/*")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    String encryptedPayload = response.body();
    return MangoCrypto.decrypt(encryptedPayload);
  }

  public String getTags() throws Exception {
    String url = BASE + "/api/filtros/tags";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Authorization", "Bearer " + token)
        .header("Accept", "*/*")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    String encryptedPayload = response.body();
    return MangoCrypto.decrypt(encryptedPayload);
  }
}