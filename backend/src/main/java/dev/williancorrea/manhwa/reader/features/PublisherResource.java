package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/publisher")
public class PublisherResource {

  private final PublisherService publisherService;

  public PublisherResource(@Lazy PublisherService publisherService) {
    this.publisherService = publisherService;
  }

  @GetMapping()
  public ResponseEntity<List<PublisherOutput>> findAll() {
    var items = publisherService.findAll()
        .stream().map(PublisherOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PublisherOutput> findById(@PathVariable UUID id) {
    var item = publisherService.findById(id)
        .map(PublisherOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
