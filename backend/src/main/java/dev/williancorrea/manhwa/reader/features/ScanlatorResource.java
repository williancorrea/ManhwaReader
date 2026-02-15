package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/scanlator")
public class ScanlatorResource {

  private final ScanlatorService scanlatorService;

  public ScanlatorResource(@Lazy ScanlatorService scanlatorService) {
    this.scanlatorService = scanlatorService;
  }

  @GetMapping()
  public ResponseEntity<List<ScanlatorOutput>> findAll() {
    var items = scanlatorService.findAll()
        .stream().map(ScanlatorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ScanlatorOutput> findById(@PathVariable UUID id) {
    var item = scanlatorService.findById(id)
        .map(ScanlatorOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
