package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;

import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MangaDexClientFallback implements MangaDexClient {

//  @Override
//  public MangaDexResponse getMangaList(Map<String, Object> queryParams) {
//    log.warn("Fallback triggered for getMangaList");
//    return createErrorResponse();
//  }

  @Override
  public MangaDexResponse getMangaById(String id, String[] includes) {
    log.warn("Fallback triggered for getMangaById with id: {}", id);
    return createErrorResponse();
  }

  @Override
  public MangaDexResponse searchManga(String title, Integer limit, Integer offset, String[] includes) {
    log.warn("Fallback triggered for searchManga with title: {}", title);
    return createErrorResponse();
  }

//  @Override
//  public MangaDexResponse getMangaByDemographic(String demographic, Integer limit, Integer offset, String[] includes) {
//    log.warn("Fallback triggered for getMangaByDemographic with demographic: {}", demographic);
//    return createErrorResponse();
//  }
//
//  @Override
//  public MangaDexResponse getMangaByStatus(String status, Integer limit, Integer offset, String[] includes) {
//    log.warn("Fallback triggered for getMangaByStatus with status: {}", status);
//    return createErrorResponse();
//  }
//
//  @Override
//  public MangaDexResponse getMangaByTags(String[] includedTags, Integer limit, Integer offset, String[] includes) {
//    log.warn("Fallback triggered for getMangaByTags");
//    return createErrorResponse();
//  }
//
//  @Override
//  public MangaDexResponse getCoverArtByManga(String[] includedTags, Integer limit, Integer offset, String[] mandaID,
//                                             String[] order) {
//    log.warn("Fallback triggered for getCoverArtByManga");
//    return createErrorResponse();
//  }

  private MangaDexResponse createErrorResponse() {
    MangaDexResponse response = new MangaDexResponse();
    response.setResult("error");
    response.setResponse("Fallback response - Service temporarily unavailable");
    return response;
  }
}