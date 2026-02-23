package dev.williancorrea.manhwa.reader.synchronization.mangadex.client;


import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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

//  @GetMapping("/manga/{id}")
//  MangaDexResponse getMangaById(
//      @PathVariable("id") String id,
//      @RequestParam Map<String, Object> includes
//  );

  @GetMapping("/manga")
  MangaDexResponse searchManga(
      @RequestParam("title") String title,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
  );

//  @GetMapping("/manga")
//  MangaDexResponse getMangaByDemographic(
//      @RequestParam("publicationDemographic[]") String demographic,
//      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
//      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
//  );
//
//  @GetMapping("/manga")
//  MangaDexResponse getMangaByStatus(
//      @RequestParam("status[]") String status,
//      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
//      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
//  );
//
//  @GetMapping("/manga")
//  MangaDexResponse getMangaByTags(
//      @RequestParam("includedTags[]") String[] includedTags,
//      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
//      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//      @RequestParam(value = "includes[]", defaultValue = "author,artist,cover_art") String[] includes
//  );
//
//  @GetMapping("/cover")
//  MangaDexResponse getCoverArtByManga(
//      @RequestParam("includedTags[]") String[] includedTags,
//      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
//      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//      @RequestParam(value = "manga[]") String[] mandaID,
//      @RequestParam(value = "order[volume]", defaultValue = "asc") String[] order
//  );

}