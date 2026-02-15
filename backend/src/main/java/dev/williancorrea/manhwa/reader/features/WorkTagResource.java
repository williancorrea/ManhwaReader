package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/work-tag")
public class WorkTagResource {

  private final WorkTagService workTagService;

  public WorkTagResource(@Lazy WorkTagService workTagService) {
    this.workTagService = workTagService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkTagOutput>> findAll() {
    var items = workTagService.findAll()
        .stream().map(WorkTagOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{workId}/{tagId}")
  public ResponseEntity<WorkTagOutput> findById(@PathVariable UUID workId, @PathVariable UUID tagId) {
    var id = new WorkTagId(workId, tagId);
    var item = workTagService.findById(id)
        .map(WorkTagOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
