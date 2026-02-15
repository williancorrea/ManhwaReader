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
@RequestMapping("features/library")
public class LibraryResource {

  private final LibraryService libraryService;

  public LibraryResource(@Lazy LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @GetMapping()
  public ResponseEntity<List<LibraryOutput>> findAll() {
    var items = libraryService.findAll()
        .stream().map(LibraryOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<LibraryOutput> create(@RequestBody @Valid LibraryInput input) {
    var entity = toEntity(input);
    var saved = libraryService.save(entity);
    return ResponseEntity.ok(new LibraryOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<LibraryOutput> findById(@PathVariable UUID id) {
    var item = libraryService.findById(id)
        .map(LibraryOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LibraryOutput> update(@PathVariable UUID id, @RequestBody @Valid LibraryInput input) {
    if (!libraryService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = libraryService.save(entity);
    return ResponseEntity.ok(new LibraryOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!libraryService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    libraryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Library toEntity(LibraryInput input) {
    var entity = new Library();
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
    entity.setStatus(input.getStatus());
    return entity;
  }
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<LibraryOutput>> findAllByUser(@PathVariable UUID userId) {
    var items = libraryService.findAllByUserId(userId)
        .stream().map(LibraryOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

