package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/alternative-title")
public class AlternativeTitleResource {

  private final AlternativeTitleService alternativeTitleService;

  public AlternativeTitleResource(@Lazy AlternativeTitleService alternativeTitleService) {
    this.alternativeTitleService = alternativeTitleService;
  }

  @GetMapping()
  public ResponseEntity<List<AlternativeTitleOutput>> findAll() {
    var items = alternativeTitleService.findAll()
        .stream().map(AlternativeTitleOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AlternativeTitleOutput> findById(@PathVariable UUID id) {
    var item = alternativeTitleService.findById(id)
        .map(AlternativeTitleOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
