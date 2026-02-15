package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

  @PostMapping()
  public ResponseEntity<WorkAuthorOutput> create(@RequestBody @Valid WorkAuthorInput input) {
    var entity = toEntity(input);
    var saved = workAuthorService.save(entity);
    return ResponseEntity.ok(new WorkAuthorOutput(saved));
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

  @PutMapping("/{workId}/{authorId}")
  public ResponseEntity<WorkAuthorOutput> update(@PathVariable UUID workId, @PathVariable UUID authorId, @RequestBody @Valid WorkAuthorInput input) {
    var id = new WorkAuthorId(workId, authorId);
    if (!workAuthorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    var work = new Work();
    work.setId(workId);
    entity.setWork(work);
    var author = new Author();
    author.setId(authorId);
    entity.setAuthor(author);
    var saved = workAuthorService.save(entity);
    return ResponseEntity.ok(new WorkAuthorOutput(saved));
  }

  @DeleteMapping("/{workId}/{authorId}")
  public ResponseEntity<Void> delete(@PathVariable UUID workId, @PathVariable UUID authorId) {
    var id = new WorkAuthorId(workId, authorId);
    if (!workAuthorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    workAuthorService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private WorkAuthor toEntity(WorkAuthorInput input) {
    var entity = new WorkAuthor();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getAuthorId() != null) {
      var author = new Author();
      author.setId(input.getAuthorId());
      entity.setAuthor(author);
    }
    entity.setRole(input.getRole());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<WorkAuthorOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = workAuthorService.findAllByWorkId(workId)
        .stream().map(WorkAuthorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

