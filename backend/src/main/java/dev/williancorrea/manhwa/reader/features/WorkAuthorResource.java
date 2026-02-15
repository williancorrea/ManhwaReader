package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/work-author")
public class WorkAuthorResource {

  private final WorkAuthorService workAuthorService;

  public WorkAuthorResource(@Lazy WorkAuthorService workAuthorService) {
    this.workAuthorService = workAuthorService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkAuthorOutput>> findAll() {
    var items = workAuthorService.findAll()
        .stream().map(WorkAuthorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{workId}/{authorId}")
  public ResponseEntity<WorkAuthorOutput> findById(@PathVariable UUID workId, @PathVariable UUID authorId) {
    var id = new WorkAuthorId(workId, authorId);
    var item = workAuthorService.findById(id)
        .map(WorkAuthorOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
