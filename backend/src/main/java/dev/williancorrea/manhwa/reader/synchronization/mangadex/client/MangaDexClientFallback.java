package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;

import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MangaDexClientFallback implements MangaDexClient {


  @Override
  public MangaDexResponse searchManga(String title, Integer limit, Integer offset, List<String> includes) {
    log.warn("Fallback triggered for searchManga with title: {}", title);
    return createErrorResponseList();
  }

  private MangaDexResponse createErrorResponseList() {
    var response = new MangaDexResponse();
    response.setResult("error");
    response.setResponse("Fallback response - Service temporarily unavailable");
    return response;
  }
}