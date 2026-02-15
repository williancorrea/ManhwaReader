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
@RequestMapping("features/scanlator")
public class ScanlatorResource {

  private final ScanlatorService scanlatorService;

  public ScanlatorResource(@Lazy ScanlatorService scanlatorService) {
    this.scanlatorService = scanlatorService;
  }

  @GetMapping()
  public ResponseEntity<List<ScanlatorOutput>> findAll() {
    var items = scanlatorService.findAll()
        .stream().map(ScanlatorOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<ScanlatorOutput> create(@RequestBody @Valid ScanlatorInput input) {
    var entity = toEntity(input);
    var saved = scanlatorService.save(entity);
    return ResponseEntity.ok(new ScanlatorOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ScanlatorOutput> findById(@PathVariable UUID id) {
    var item = scanlatorService.findById(id)
        .map(ScanlatorOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ScanlatorOutput> update(@PathVariable UUID id, @RequestBody @Valid ScanlatorInput input) {
    if (!scanlatorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = scanlatorService.save(entity);
    return ResponseEntity.ok(new ScanlatorOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!scanlatorService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    scanlatorService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Scanlator toEntity(ScanlatorInput input) {
    var entity = new Scanlator();
    entity.setName(input.getName());
    entity.setWebsite(input.getWebsite());
    return entity;
  }
}


