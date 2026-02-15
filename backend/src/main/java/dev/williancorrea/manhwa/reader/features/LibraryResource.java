package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/library")
public class LibraryResource {

  private final LibraryService libraryService;

  public LibraryResource(@Lazy LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @GetMapping()
  public ResponseEntity<List<LibraryOutput>> findAll() {
    var items = libraryService.findAll()
        .stream().map(LibraryOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LibraryOutput> findById(@PathVariable UUID id) {
    var item = libraryService.findById(id)
        .map(LibraryOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
