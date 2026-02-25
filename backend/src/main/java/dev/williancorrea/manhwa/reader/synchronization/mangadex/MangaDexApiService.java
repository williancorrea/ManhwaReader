package dev.williancorrea.manhwa.reader.synchronization.mangadex;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.client.MangaDexClient;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexData;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MangaDexApiService {

  private final MangaDexClient mangaDexClient;
  private final MangaDexMapperService mapperService;
  private final WorkService workService;

  @Transactional
  public void searchMangaFromExternalApi(String title, Pageable pageable) {
    try {
      log.info("--> Searching manga from MangaDex API - ({})", title.toUpperCase());
      MangaDexResponse response = mangaDexClient.searchManga(
          title,
          pageable.getPageSize(),
          pageable.getPageNumber() * pageable.getPageSize(),
          List.of("artist", "author", "cover_art")
      );

      if ("ok".equals(response.getResult()) && response.getData() != null) {
        log.info("--> Found {} manga from MangaDex API", response.getData().size());
        response.getData().forEach(this::startSynchronization);
      } else {
        log.warn("--> No manga found from MangaDex API");
      }
      log.info("<-- Finished search manga from MangaDex API");
    } catch (FeignException e) {
      log.error("Error searchMangaFromExternalApi manga from MangaDex API: {}", e.getMessage());
      //TODO: Implementar um tratamento mais adequado de exceções
      throw new RuntimeException("Failed to search manga from MangaDex API", e);
    }
  }

  @Transactional
  protected void startSynchronization(MangaDexData data) {
    Work work = mapperService.toEntity(data);
    workService.save(work);
    log.info("--> Manga imported success - {}", work.getTitles().getFirst().getTitle());
  }

}