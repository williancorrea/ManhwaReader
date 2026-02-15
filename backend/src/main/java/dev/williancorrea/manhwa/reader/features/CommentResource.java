package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/comment")
public class CommentResource {

  private final CommentService commentService;

  public CommentResource(@Lazy CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping()
  public ResponseEntity<List<CommentOutput>> findAll() {
    var items = commentService.findAll()
        .stream().map(CommentOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CommentOutput> findById(@PathVariable UUID id) {
    var item = commentService.findById(id)
        .map(CommentOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
