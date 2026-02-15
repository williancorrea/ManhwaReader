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
@RequestMapping("features/author")
public class AuthorResource {

  private final AuthorService authorService;

  public AuthorResource(@Lazy AuthorService authorService) {
    this.authorService = authorService;
  }

  @GetMapping()
  public ResponseEntity<List<AuthorOutput>> findAll() {
    var items = authorService.findAll()
        .stream().map(AuthorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<AuthorOutput> create(@RequestBody @Valid AuthorInput input) {
    var entity = toEntity(input);
    var saved = authorService.save(entity);
    return ResponseEntity.ok(new AuthorOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorOutput> findById(@PathVariable UUID id) {
    var item = authorService.findById(id)
        .map(AuthorOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AuthorOutput> update(@PathVariable UUID id, @RequestBody @Valid AuthorInput input) {
    if (!authorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = authorService.save(entity);
    return ResponseEntity.ok(new AuthorOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!authorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    authorService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Author toEntity(AuthorInput input) {
    var entity = new Author();
    entity.setName(input.getName());
    entity.setType(input.getType());
    return entity;
  }
}


