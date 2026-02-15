package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/file")
public class FileStorageResource {

  private final FileStorageService fileStorageService;

  public FileStorageResource(@Lazy FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @GetMapping()
  public ResponseEntity<List<FileStorageOutput>> findAll() {
    var items = fileStorageService.findAll()
        .stream().map(FileStorageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileStorageOutput> findById(@PathVariable UUID id) {
    var item = fileStorageService.findById(id)
        .map(FileStorageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
