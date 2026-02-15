package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/reading-progress")
public class ReadingProgressResource {

  private final ReadingProgressService readingProgressService;

  public ReadingProgressResource(@Lazy ReadingProgressService readingProgressService) {
    this.readingProgressService = readingProgressService;
  }

  @GetMapping()
  public ResponseEntity<List<ReadingProgressOutput>> findAll() {
    var items = readingProgressService.findAll()
        .stream().map(ReadingProgressOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReadingProgressOutput> findById(@PathVariable UUID id) {
    var item = readingProgressService.findById(id)
        .map(ReadingProgressOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
