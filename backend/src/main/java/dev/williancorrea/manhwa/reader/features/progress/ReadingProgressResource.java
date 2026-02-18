package dev.williancorrea.manhwa.reader.features.progress;

import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import jakarta.validation.Valid;
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
@RequestMapping("features/reading-progress")
public class ReadingProgressResource {

  private final ReadingProgressService readingProgressService;

  public ReadingProgressResource(@Lazy ReadingProgressService readingProgressService) {
    this.readingProgressService = readingProgressService;
  }

  @GetMapping()
  public ResponseEntity<List<ReadingProgressOutput>> findAll() {
    var items = readingProgressService.findAll()
        .stream().map(ReadingProgressOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<ReadingProgressOutput> create(@RequestBody @Valid ReadingProgressInput input) {
    var entity = toEntity(input);
    var saved = readingProgressService.save(entity);
    return ResponseEntity.ok(new ReadingProgressOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReadingProgressOutput> findById(@PathVariable UUID id) {
    var item = readingProgressService.findById(id)
        .map(ReadingProgressOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ReadingProgressOutput> update(@PathVariable UUID id, @RequestBody @Valid ReadingProgressInput input) {
    if (!readingProgressService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = readingProgressService.save(entity);
    return ResponseEntity.ok(new ReadingProgressOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!readingProgressService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    readingProgressService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private ReadingProgress toEntity(ReadingProgressInput input) {
    var entity = new ReadingProgress();
    if (input.getUserId() != null) {
      var user = new User();
      user.setId(input.getUserId());
      entity.setUser(user);
    }
    if (input.getChapterId() != null) {
      var chapter = new Chapter();
      chapter.setId(input.getChapterId());
      entity.setChapter(chapter);
    }
    entity.setPageNumber(input.getPageNumber());
    entity.setLastReadAt(input.getLastReadAt());
    return entity;
  }
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ReadingProgressOutput>> findAllByUser(@PathVariable UUID userId) {
    var items = readingProgressService.findAllByUserId(userId)
        .stream().map(ReadingProgressOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

