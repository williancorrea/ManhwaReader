package dev.williancorrea.manhwa.reader.scraper.mangadex.client;

import java.util.List;
import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexResponseData;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexResponseList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "mangaDexClient",
    url = "${synchronization.mangadex.api.url}",
    configuration = FeignConfig.class
)
public interface MangaDexClient {

  @GetMapping("/manga")
  MangaDexResponseList searchManga(
      @RequestParam("title") String title,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "includes[]", required = false) List<String> includes
  );

  @GetMapping("/manga/{mangaId}")
  MangaDexResponseData getMangaById(
      @PathVariable("mangaId") String mangaId,
      @RequestParam(value = "includes[]", required = false) List<String> includes
  );
}