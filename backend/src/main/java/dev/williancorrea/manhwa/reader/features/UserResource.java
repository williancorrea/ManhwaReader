package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

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
}
