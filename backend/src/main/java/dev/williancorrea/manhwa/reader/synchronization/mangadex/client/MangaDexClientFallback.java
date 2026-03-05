package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;

import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponseData;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponseList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MangaDexClientFallback implements MangaDexClient {

  @Override
  public MangaDexResponseList searchManga(String title, Integer limit, Integer offset, List<String> includes) {
    log.warn("Fallback triggered for searchManga with title: {}", title);
    return createErrorResponseList();
  }

  @Override
  public MangaDexResponseData getMangaById(String mangaId, List<String> includes) {
    log.warn("Fallback triggered for getMangaById with mangaId: {}", mangaId);
    return createErrorResponseData();
  }

  private MangaDexResponseList createErrorResponseList() {
    var response = new MangaDexResponseList();
    response.setResult("error");
    response.setResponse("Fallback response - Service temporarily unavailable");
    return response;
  }

  private MangaDexResponseData createErrorResponseData() {
    var response = new MangaDexResponseData();
    response.setResult("error");
    response.setResponse("Fallback response - Service temporarily unavailable");
    return response;
  }
}