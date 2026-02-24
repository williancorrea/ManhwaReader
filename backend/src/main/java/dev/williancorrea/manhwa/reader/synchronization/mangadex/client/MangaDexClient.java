package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;


import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//TODO: VERIFICAR
@FeignClient(
    name = "mangaDexClient",
//    url = "https://api.mangadex.org",
    url = "http://localhost:3000",
    configuration = FeignConfig.class
)
public interface MangaDexClient {

//  @GetMapping("/manga")
//  MangaDexResponse getMangaList(
//      @RequestParam Map<String, Object> queryParams
//  );

  @GetMapping("/manga/{id}")
  MangaDexResponse getMangaById(
      @PathVariable("id") String id,
      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
  );

  @GetMapping("/manga")
  MangaDexResponse searchManga(
      @RequestParam("title") String title,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
  );
  
}