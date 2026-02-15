package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/work-title")
public class WorkTitleResource {

  private final WorkTitleService workTitleService;

  public WorkTitleResource(@Lazy WorkTitleService workTitleService) {
    this.workTitleService = workTitleService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkTitleOutput>> findAll() {
    var items = workTitleService.findAll()
        .stream().map(WorkTitleOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkTitleOutput> findById(@PathVariable UUID id) {
    var item = workTitleService.findById(id)
        .map(WorkTitleOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
