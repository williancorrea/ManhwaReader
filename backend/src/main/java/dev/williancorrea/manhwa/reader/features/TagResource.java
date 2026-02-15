package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/tag")
public class TagResource {

  private final TagService tagService;

  public TagResource(@Lazy TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping()
  public ResponseEntity<List<TagOutput>> findAll() {
    var items = tagService.findAll()
        .stream().map(TagOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagOutput> findById(@PathVariable UUID id) {
    var item = tagService.findById(id)
        .map(TagOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
