package dev.williancorrea.manhwa.reader.synchronization.mangadex;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("synchronization/mangadex")
//@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
public class MangaDexResource {

  private final MangaDexApiService mangaDexApiService;

  public MangaDexResource(@Lazy MangaDexApiService mangaDexApiService) {
    this.mangaDexApiService = mangaDexApiService;
  }

  @GetMapping
  public void searchMangaFromExternalApi(@RequestParam @NotNull @NotEmpty String title) {
    mangaDexApiService.searchMangaFromExternalApi(title, Pageable.ofSize(5));
  }
}



