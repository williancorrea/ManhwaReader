package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/author")
public class AuthorResource {

  private final AuthorService authorService;

  public AuthorResource(@Lazy AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping()
  public ResponseEntity<List<AuthorOutput>> findAll() {
    var items = authorService.findAll()
        .stream().map(AuthorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorOutput> findById(@PathVariable UUID id) {
    var item = authorService.findById(id)
        .map(AuthorOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
