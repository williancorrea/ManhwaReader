package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/work")
public class WorkResource {

  private final WorkService workService;

  public WorkResource(@Lazy WorkService workService) {
    this.workService = workService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkOutput>> findAll() {
    var items = workService.findAll()
        .stream().map(WorkOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkOutput> findById(@PathVariable UUID id) {
    var item = workService.findById(id)
        .map(WorkOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
