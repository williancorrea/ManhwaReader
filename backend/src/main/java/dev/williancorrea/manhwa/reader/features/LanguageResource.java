package dev.williancorrea.manhwa.reader.features;

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
@RequestMapping("features/language")
public class LanguageResource {

  private final LanguageService languageService;

  public LanguageResource(@Lazy LanguageService languageService) {
    this.languageService = languageService;
  }

  @GetMapping()
  public ResponseEntity<List<LanguageOutput>> findAll() {
    var items = languageService.findAll()
        .stream().map(LanguageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<LanguageOutput> create(@RequestBody LanguageInput input) {
    var entity = toEntity(input);
    var saved = languageService.save(entity);
    return ResponseEntity.ok(new LanguageOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<LanguageOutput> findById(@PathVariable UUID id) {
    var item = languageService.findById(id)
        .map(LanguageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LanguageOutput> update(@PathVariable UUID id, @RequestBody LanguageInput input) {
    if (!languageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = languageService.save(entity);
    return ResponseEntity.ok(new LanguageOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!languageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    languageService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Language toEntity(LanguageInput input) {
    var entity = new Language();
    entity.setCode(input.getCode());
    entity.setName(input.getName());
    return entity;
  }
}

