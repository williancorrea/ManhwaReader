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
@RequestMapping("features/access-group")
public class AccessGroupResource {

  private final AccessGroupService accessGroupService;

  public AccessGroupResource(@Lazy AccessGroupService accessGroupService) {
    this.accessGroupService = accessGroupService;
  }

  @GetMapping()
  public ResponseEntity<List<AccessGroupOutput>> findAll() {
    var items = accessGroupService.findAll()
        .stream().map(AccessGroupOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<AccessGroupOutput> create(@RequestBody @Valid AccessGroupInput input) {
    var entity = toEntity(input);
    var saved = accessGroupService.save(entity);
    return ResponseEntity.ok(new AccessGroupOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AccessGroupOutput> findById(@PathVariable UUID id) {
    var item = accessGroupService.findById(id)
        .map(AccessGroupOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccessGroupOutput> update(@PathVariable UUID id, @RequestBody @Valid AccessGroupInput input) {
    if (!accessGroupService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = accessGroupService.save(entity);
    return ResponseEntity.ok(new AccessGroupOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!accessGroupService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    accessGroupService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private AccessGroup toEntity(AccessGroupInput input) {
    var entity = new AccessGroup();
    entity.setName(input.getName());
    return entity;
  }
}
