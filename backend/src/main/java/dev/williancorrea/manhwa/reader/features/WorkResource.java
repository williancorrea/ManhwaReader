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

  @PostMapping()
  public ResponseEntity<WorkOutput> create(@RequestBody @Valid WorkInput input) {
    var entity = toEntity(input);
    var saved = workService.save(entity);
    return ResponseEntity.ok(new WorkOutput(saved));
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

  @PutMapping("/{id}")
  public ResponseEntity<WorkOutput> update(@PathVariable UUID id, @RequestBody @Valid WorkInput input) {
    if (!workService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = workService.save(entity);
    return ResponseEntity.ok(new WorkOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!workService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    workService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Work toEntity(WorkInput input) {
    var entity = new Work();
    entity.setOriginalTitle(input.getOriginalTitle());
    entity.setSynopsis(input.getSynopsis());
    entity.setType(input.getType());
    entity.setStatus(input.getStatus());
    entity.setReleaseYear(input.getReleaseYear());
    if (input.getCoverImageId() != null) {
      var file = new FileStorage();
      file.setId(input.getCoverImageId());
      entity.setCoverImage(file);
    }
    if (input.getPublisherId() != null) {
      var publisher = new Publisher();
      publisher.setId(input.getPublisherId());
      entity.setPublisher(publisher);
    }
    entity.setCreatedAt(input.getCreatedAt());
    entity.setUpdatedAt(input.getUpdatedAt());
    return entity;
  }
  @GetMapping("/status/{status}")
  public ResponseEntity<List<WorkOutput>> findAllByStatus(@PathVariable WorkStatus status) {
    var items = workService.findAllByStatus(status)
        .stream().map(WorkOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<WorkOutput>> findAllByType(@PathVariable WorkType type) {
    var items = workService.findAllByType(type)
        .stream().map(WorkOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

