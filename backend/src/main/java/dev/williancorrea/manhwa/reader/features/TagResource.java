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
@RequestMapping("features/tag")
public class TagResource {

  private final TagService tagService;

  public TagResource(@Lazy TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping()
  public ResponseEntity<List<TagOutput>> findAll() {
    var items = tagService.findAll()
        .stream().map(TagOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<TagOutput> create(@RequestBody @Valid TagInput input) {
    var entity = toEntity(input);
    var saved = tagService.save(entity);
    return ResponseEntity.ok(new TagOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagOutput> findById(@PathVariable UUID id) {
    var item = tagService.findById(id)
        .map(TagOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TagOutput> update(@PathVariable UUID id, @RequestBody @Valid TagInput input) {
    if (!tagService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = tagService.save(entity);
    return ResponseEntity.ok(new TagOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!tagService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    tagService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Tag toEntity(TagInput input) {
    var entity = new Tag();
    entity.setName(input.getName());
    entity.setIsNsfw(input.getIsNsfw());
    return entity;
  }
}


