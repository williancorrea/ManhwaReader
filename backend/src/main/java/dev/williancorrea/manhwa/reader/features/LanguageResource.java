package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/language")
public class LanguageResource {

  private final LanguageService languageService;

  public LanguageResource(@Lazy LanguageService languageService) {
    this.languageService = languageService;
  }

  @GetMapping()
  public ResponseEntity<List<LanguageOutput>> findAll() {
    var items = languageService.findAll()
        .stream().map(LanguageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LanguageOutput> findById(@PathVariable UUID id) {
    var item = languageService.findById(id)
        .map(LanguageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
