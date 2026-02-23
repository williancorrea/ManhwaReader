package dev.williancorrea.manhwa.reader.synchronization.mangadex;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("synchronization/mangadex")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
public class MangaDexResource {

  private final MangaDexApiService mangaDexApiService;

  public MangaDexResource(@Lazy MangaDexApiService mangaDexApiService) {
    this.mangaDexApiService = mangaDexApiService;
  }

  @GetMapping()
  public void test() {
    mangaDexApiService.searchMangaFromExternalApi("O cavaleiro em eterna regressão", Pageable.ofSize(5));
  }
}



