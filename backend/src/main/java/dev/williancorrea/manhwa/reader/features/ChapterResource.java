package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/chapter")
public class ChapterResource {

  private final ChapterService chapterService;

  public ChapterResource(@Lazy ChapterService chapterService) {
    this.chapterService = chapterService;
  }

  @GetMapping()
  public ResponseEntity<List<ChapterOutput>> findAll() {
    var items = chapterService.findAll()
        .stream().map(ChapterOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChapterOutput> findById(@PathVariable UUID id) {
    var item = chapterService.findById(id)
        .map(ChapterOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
