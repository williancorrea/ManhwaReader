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
@RequestMapping("features/genre")
public class GenreResource {

  private final GenreService genreService;

  public GenreResource(@Lazy GenreService genreService) {
    this.genreService = genreService;
  }

  @GetMapping()
  public ResponseEntity<List<GenreOutput>> findAll() {
    var items = genreService.findAll()
        .stream().map(GenreOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<GenreOutput> create(@RequestBody @Valid GenreInput input) {
    var entity = toEntity(input);
    var saved = genreService.save(entity);
    return ResponseEntity.ok(new GenreOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GenreOutput> findById(@PathVariable UUID id) {
    var item = genreService.findById(id)
        .map(GenreOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GenreOutput> update(@PathVariable UUID id, @RequestBody @Valid GenreInput input) {
    if (!genreService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = genreService.save(entity);
    return ResponseEntity.ok(new GenreOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!genreService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    genreService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Genre toEntity(GenreInput input) {
    var entity = new Genre();
    entity.setName(input.getName());
    return entity;
  }
}


