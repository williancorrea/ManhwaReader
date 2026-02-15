package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/page")
public class PageResource {

  private final PageService pageService;

  public PageResource(@Lazy PageService pageService) {
    this.pageService = pageService;
  }

  @GetMapping()
  public ResponseEntity<List<PageOutput>> findAll() {
    var items = pageService.findAll()
        .stream().map(PageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PageOutput> findById(@PathVariable UUID id) {
    var item = pageService.findById(id)
        .map(PageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
