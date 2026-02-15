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
@RequestMapping("features/chapter")
public class ChapterResource {

  private final ChapterService chapterService;

  public ChapterResource(@Lazy ChapterService chapterService) {
    this.chapterService = chapterService;
  }

  @GetMapping()
  public ResponseEntity<List<ChapterOutput>> findAll() {
    var items = chapterService.findAll()
        .stream().map(ChapterOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<ChapterOutput> create(@RequestBody @Valid ChapterInput input) {
    var entity = toEntity(input);
    var saved = chapterService.save(entity);
    return ResponseEntity.ok(new ChapterOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ChapterOutput> findById(@PathVariable UUID id) {
    var item = chapterService.findById(id)
        .map(ChapterOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ChapterOutput> update(@PathVariable UUID id, @RequestBody @Valid ChapterInput input) {
    if (!chapterService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = chapterService.save(entity);
    return ResponseEntity.ok(new ChapterOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!chapterService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    chapterService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Chapter toEntity(ChapterInput input) {
    var entity = new Chapter();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getVolumeId() != null) {
      var volume = new Volume();
      volume.setId(input.getVolumeId());
      entity.setVolume(volume);
    }
    if (input.getLanguageId() != null) {
      var language = new Language();
      language.setId(input.getLanguageId());
      entity.setLanguage(language);
    }
    if (input.getScanlatorId() != null) {
      var scanlator = new Scanlator();
      scanlator.setId(input.getScanlatorId());
      entity.setScanlator(scanlator);
    }
    entity.setNumber(input.getNumber());
    entity.setTitle(input.getTitle());
    entity.setReleaseDate(input.getReleaseDate());
    entity.setCreatedAt(input.getCreatedAt());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<ChapterOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = chapterService.findAllByWorkId(workId)
        .stream().map(ChapterOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

