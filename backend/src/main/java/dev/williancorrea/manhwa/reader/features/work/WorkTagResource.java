package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.tag.Tag;
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
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("features/work-tag")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
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

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PostMapping()
  public ResponseEntity<WorkTagOutput> create(@RequestBody @Valid WorkTagInput input) {
    var entity = toEntity(input);
    var saved = workTagService.save(entity);
    return ResponseEntity.ok(new WorkTagOutput(saved));
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

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PutMapping("/{workId}/{tagId}")
  public ResponseEntity<WorkTagOutput> update(@PathVariable UUID workId, @PathVariable UUID tagId, @RequestBody @Valid WorkTagInput input) {
    var id = new WorkTagId(workId, tagId);
    if (!workTagService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    var work = new Work();
    work.setId(workId);
    entity.setWork(work);
    var tag = new Tag();
    tag.setId(tagId);
    entity.setTag(tag);
    var saved = workTagService.save(entity);
    return ResponseEntity.ok(new WorkTagOutput(saved));
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @DeleteMapping("/{workId}/{tagId}")
  public ResponseEntity<Void> delete(@PathVariable UUID workId, @PathVariable UUID tagId) {
    var id = new WorkTagId(workId, tagId);
    if (!workTagService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    workTagService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private WorkTag toEntity(WorkTagInput input) {
    var entity = new WorkTag();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getTagId() != null) {
      var tag = new Tag();
      tag.setId(input.getTagId());
      entity.setTag(tag);
    }
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<WorkTagOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = workTagService.findAllByWorkId(workId)
        .stream().map(WorkTagOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}



