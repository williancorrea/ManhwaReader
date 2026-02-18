package dev.williancorrea.manhwa.reader.features.comments;

import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.work.Work;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @PostMapping()
  public ResponseEntity<CommentOutput> create(@RequestBody @Valid CommentInput input) {
    var entity = toEntity(input);
    var saved = commentService.save(entity);
    return ResponseEntity.ok(new CommentOutput(saved));
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

  @PutMapping("/{id}")
  public ResponseEntity<CommentOutput> update(@PathVariable UUID id, @RequestBody @Valid CommentInput input) {
    if (!commentService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = commentService.save(entity);
    return ResponseEntity.ok(new CommentOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!commentService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    commentService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Comment toEntity(CommentInput input) {
    var entity = new Comment();
    if (input.getUserId() != null) {
      var user = new User();
      user.setId(input.getUserId());
      entity.setUser(user);
    }
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getChapterId() != null) {
      var chapter = new Chapter();
      chapter.setId(input.getChapterId());
      entity.setChapter(chapter);
    }
    entity.setContent(input.getContent());
    entity.setCreatedAt(input.getCreatedAt());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<CommentOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = commentService.findAllByWorkId(workId)
        .stream().map(CommentOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/chapter/{chapterId}")
  public ResponseEntity<List<CommentOutput>> findAllByChapter(@PathVariable UUID chapterId) {
    var items = commentService.findAllByChapterId(chapterId)
        .stream().map(CommentOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

