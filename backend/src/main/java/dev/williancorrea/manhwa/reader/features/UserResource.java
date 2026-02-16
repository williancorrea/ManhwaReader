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
@RequestMapping("features/user")
public class UserResource {

  private final UserService userService;

  public UserResource(@Lazy UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  public ResponseEntity<List<UserOutput>> findAll() {
    var items = userService.findAll()
        .stream().map(UserOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<UserOutput> create(@RequestBody @Valid UserInput input) {
    var entity = toEntity(input);
    var saved = userService.save(entity);
    return ResponseEntity.ok(new UserOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserOutput> findById(@PathVariable UUID id) {
    var item = userService.findById(id)
        .map(UserOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserOutput> update(@PathVariable UUID id, @RequestBody @Valid UserInput input) {
    if (!userService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = userService.save(entity);
    return ResponseEntity.ok(new UserOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!userService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private User toEntity(UserInput input) {
    var entity = new User();
    entity.setUsername(input.getUsername());
    entity.setEmail(input.getEmail());
    entity.setPasswordHash(input.getPasswordHash());
    entity.setCreatedAt(input.getCreatedAt());
    return entity;
  }
}


