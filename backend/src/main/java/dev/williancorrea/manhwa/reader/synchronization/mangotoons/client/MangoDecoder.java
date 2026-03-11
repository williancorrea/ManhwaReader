package dev.williancorrea.manhwa.reader.synchronization.mangotoons.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import dev.williancorrea.manhwa.reader.synchronization.mangotoons.MangoCrypto;
import feign.Response;
import feign.codec.Decoder;

public class MangoDecoder implements Decoder {

  private final ObjectMapper objectMapper;

  public MangoDecoder(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Object decode(Response response, Type type) throws IOException {

    String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

    boolean encrypted =
        response.headers().containsKey("x-encrypted");

    if (encrypted) {
      try {
        body = MangoCrypto.decrypt(body);
      } catch (Exception e) {
        throw new RuntimeException("Erro ao descriptografar resposta MangoToons", e);
      }
    }

    return objectMapper.readValue(body, objectMapper.constructType(type));
  }
}