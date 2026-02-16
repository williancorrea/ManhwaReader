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
@RequestMapping("features/access-group-permission")
public class AccessGroupPermissionResource {

  private final AccessGroupPermissionService accessGroupPermissionService;

  public AccessGroupPermissionResource(@Lazy AccessGroupPermissionService accessGroupPermissionService) {
    this.accessGroupPermissionService = accessGroupPermissionService;
  }

  @GetMapping()
  public ResponseEntity<List<AccessGroupPermissionOutput>> findAll() {
    var items = accessGroupPermissionService.findAll()
        .stream().map(AccessGroupPermissionOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<AccessGroupPermissionOutput> create(@RequestBody @Valid AccessGroupPermissionInput input) {
    var entity = toEntity(input);
    var saved = accessGroupPermissionService.save(entity);
    return ResponseEntity.ok(new AccessGroupPermissionOutput(saved));
  }

  @GetMapping("/{accessGroupId}/{permissionId}")
  public ResponseEntity<AccessGroupPermissionOutput> findById(@PathVariable UUID accessGroupId, @PathVariable UUID permissionId) {
    var id = new AccessGroupPermissionId(accessGroupId, permissionId);
    var item = accessGroupPermissionService.findById(id)
        .map(AccessGroupPermissionOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{accessGroupId}/{permissionId}")
  public ResponseEntity<AccessGroupPermissionOutput> update(@PathVariable UUID accessGroupId, @PathVariable UUID permissionId, @RequestBody @Valid AccessGroupPermissionInput input) {
    var id = new AccessGroupPermissionId(accessGroupId, permissionId);
    if (!accessGroupPermissionService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    var accessGroup = new AccessGroup();
    accessGroup.setId(accessGroupId);
    entity.setAccessGroup(accessGroup);
    var permission = new Permission();
    permission.setId(permissionId);
    entity.setPermission(permission);
    var saved = accessGroupPermissionService.save(entity);
    return ResponseEntity.ok(new AccessGroupPermissionOutput(saved));
  }

  @DeleteMapping("/{accessGroupId}/{permissionId}")
  public ResponseEntity<Void> delete(@PathVariable UUID accessGroupId, @PathVariable UUID permissionId) {
    var id = new AccessGroupPermissionId(accessGroupId, permissionId);
    if (!accessGroupPermissionService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    accessGroupPermissionService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/access-group/{accessGroupId}")
  public ResponseEntity<List<AccessGroupPermissionOutput>> findAllByAccessGroup(@PathVariable UUID accessGroupId) {
    var items = accessGroupPermissionService.findAllByAccessGroupId(accessGroupId)
        .stream().map(AccessGroupPermissionOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/permission/{permissionId}")
  public ResponseEntity<List<AccessGroupPermissionOutput>> findAllByPermission(@PathVariable UUID permissionId) {
    var items = accessGroupPermissionService.findAllByPermissionId(permissionId)
        .stream().map(AccessGroupPermissionOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  private AccessGroupPermission toEntity(AccessGroupPermissionInput input) {
    var entity = new AccessGroupPermission();
    if (input.getAccessGroupId() != null) {
      var accessGroup = new AccessGroup();
      accessGroup.setId(input.getAccessGroupId());
      entity.setAccessGroup(accessGroup);
    }
    if (input.getPermissionId() != null) {
      var permission = new Permission();
      permission.setId(input.getPermissionId());
      entity.setPermission(permission);
    }
    return entity;
  }
}
