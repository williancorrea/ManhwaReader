package dev.williancorrea.manhwa.reader.features.access.permission;

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
@RequestMapping("features/permission")
public class PermissionResource {

  private final PermissionService permissionService;

  public PermissionResource(@Lazy PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping()
  public ResponseEntity<List<PermissionOutput>> findAll() {
    var items = permissionService.findAll()
        .stream().map(PermissionOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<PermissionOutput> create(@RequestBody @Valid PermissionInput input) {
    var entity = toEntity(input);
    var saved = permissionService.save(entity);
    return ResponseEntity.ok(new PermissionOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PermissionOutput> findById(@PathVariable UUID id) {
    var item = permissionService.findById(id)
        .map(PermissionOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PermissionOutput> update(@PathVariable UUID id, @RequestBody @Valid PermissionInput input) {
    if (!permissionService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = permissionService.save(entity);
    return ResponseEntity.ok(new PermissionOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!permissionService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    permissionService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Permission toEntity(PermissionInput input) {
    var entity = new Permission();
    entity.setName(input.getName());
    return entity;
  }
}
