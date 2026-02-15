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
@RequestMapping("features/alternative-title")
public class AlternativeTitleResource {

  private final AlternativeTitleService alternativeTitleService;

  public AlternativeTitleResource(@Lazy AlternativeTitleService alternativeTitleService) {
    this.alternativeTitleService = alternativeTitleService;
  }

  @GetMapping()
  public ResponseEntity<List<AlternativeTitleOutput>> findAll() {
    var items = alternativeTitleService.findAll()
        .stream().map(AlternativeTitleOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<AlternativeTitleOutput> create(@RequestBody AlternativeTitleInput input) {
    var entity = toEntity(input);
    var saved = alternativeTitleService.save(entity);
    return ResponseEntity.ok(new AlternativeTitleOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AlternativeTitleOutput> findById(@PathVariable UUID id) {
    var item = alternativeTitleService.findById(id)
        .map(AlternativeTitleOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AlternativeTitleOutput> update(@PathVariable UUID id, @RequestBody AlternativeTitleInput input) {
    if (!alternativeTitleService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = alternativeTitleService.save(entity);
    return ResponseEntity.ok(new AlternativeTitleOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!alternativeTitleService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    alternativeTitleService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private AlternativeTitle toEntity(AlternativeTitleInput input) {
    var entity = new AlternativeTitle();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getLanguageId() != null) {
      var language = new Language();
      language.setId(input.getLanguageId());
      entity.setLanguage(language);
    }
    entity.setTitle(input.getTitle());
    return entity;
  }
}

