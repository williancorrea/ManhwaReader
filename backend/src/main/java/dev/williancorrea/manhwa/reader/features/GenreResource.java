package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/genre")
public class GenreResource {

  private final GenreService genreService;

  public GenreResource(@Lazy GenreService genreService) {
    this.genreService = genreService;
  }

  @GetMapping()
  public ResponseEntity<List<GenreOutput>> findAll() {
    var items = genreService.findAll()
        .stream().map(GenreOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenreOutput> findById(@PathVariable UUID id) {
    var item = genreService.findById(id)
        .map(GenreOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
