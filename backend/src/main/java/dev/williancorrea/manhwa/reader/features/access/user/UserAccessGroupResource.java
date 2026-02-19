package dev.williancorrea.manhwa.reader.features.access.user;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroup;
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
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("features/user-access-group")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
public class UserAccessGroupResource {

  private final UserAccessGroupService userAccessGroupService;

  public UserAccessGroupResource(@Lazy UserAccessGroupService userAccessGroupService) {
    this.userAccessGroupService = userAccessGroupService;
  }

  @GetMapping()
  public ResponseEntity<List<UserAccessGroupOutput>> findAll() {
    var items = userAccessGroupService.findAll()
        .stream().map(UserAccessGroupOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<UserAccessGroupOutput> create(@RequestBody @Valid UserAccessGroupInput input) {
    var entity = toEntity(input);
    var saved = userAccessGroupService.save(entity);
    return ResponseEntity.ok(new UserAccessGroupOutput(saved));
  }

  @GetMapping("/{userId}/{accessGroupId}")
  public ResponseEntity<UserAccessGroupOutput> findById(@PathVariable UUID userId, @PathVariable UUID accessGroupId) {
    var id = new UserAccessGroupId(userId, accessGroupId);
    var item = userAccessGroupService.findById(id)
        .map(UserAccessGroupOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{userId}/{accessGroupId}")
  public ResponseEntity<UserAccessGroupOutput> update(@PathVariable UUID userId, @PathVariable UUID accessGroupId, @RequestBody @Valid UserAccessGroupInput input) {
    var id = new UserAccessGroupId(userId, accessGroupId);
    if (!userAccessGroupService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    var user = new User();
    user.setId(userId);
    entity.setUser(user);
    var accessGroup = new AccessGroup();
    accessGroup.setId(accessGroupId);
    entity.setAccessGroup(accessGroup);
    var saved = userAccessGroupService.save(entity);
    return ResponseEntity.ok(new UserAccessGroupOutput(saved));
  }

  @DeleteMapping("/{userId}/{accessGroupId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId, @PathVariable UUID accessGroupId) {
    var id = new UserAccessGroupId(userId, accessGroupId);
    if (!userAccessGroupService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    userAccessGroupService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<UserAccessGroupOutput>> findAllByUser(@PathVariable UUID userId) {
    var items = userAccessGroupService.findAllByUserId(userId)
        .stream().map(UserAccessGroupOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/access-group/{accessGroupId}")
  public ResponseEntity<List<UserAccessGroupOutput>> findAllByAccessGroup(@PathVariable UUID accessGroupId) {
    var items = userAccessGroupService.findAllByAccessGroupId(accessGroupId)
        .stream().map(UserAccessGroupOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  private UserAccessGroup toEntity(UserAccessGroupInput input) {
    var entity = new UserAccessGroup();
    if (input.getUserId() != null) {
      var user = new User();
      user.setId(input.getUserId());
      entity.setUser(user);
    }
    if (input.getAccessGroupId() != null) {
      var accessGroup = new AccessGroup();
      accessGroup.setId(input.getAccessGroupId());
      entity.setAccessGroup(accessGroup);
    }
    return entity;
  }
}

