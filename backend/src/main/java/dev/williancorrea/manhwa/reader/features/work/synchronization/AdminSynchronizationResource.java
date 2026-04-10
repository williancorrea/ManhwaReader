package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/synchronization")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
@RequiredArgsConstructor
public class AdminSynchronizationResource {

  private final AdminSynchronizationService service;

  @GetMapping("/works")
  public ResponseEntity<Page<AdminWorkOutput>> listWorks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String title) {
    size = Math.min(size, 50);
    return ResponseEntity.ok(service.listWorks(title, PageRequest.of(page, size)));
  }

  @GetMapping("/mangadex/search")
  public ResponseEntity<List<MangaDexSearchOutput>> searchMangaDex(
      @RequestParam @NotBlank String title,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    size = Math.min(size, 20);
    return ResponseEntity.ok(service.searchMangaDex(title, page, size));
  }

  @PostMapping("/mangadex/link")
  public ResponseEntity<Void> linkWorkToMangaDex(@RequestBody @Valid LinkWorkInput input) {
    service.linkWorkToMangaDex(input.workId(), input.mangaDexId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
