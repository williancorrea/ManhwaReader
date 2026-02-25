package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;

import java.util.List;
import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "mangaDexClient",
    url = "https://api.mangadex.org",
    configuration = FeignConfig.class
)
public interface MangaDexClient {

  @GetMapping("/manga")
  MangaDexResponse searchManga(
      @RequestParam("title") String title,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "includes[]", required = false) List<String> includes
  );

}