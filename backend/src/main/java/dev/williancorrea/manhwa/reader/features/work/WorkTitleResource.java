package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
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
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("features/work-title")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
public class WorkTitleResource {

  private final WorkTitleService workTitleService;

  public WorkTitleResource(@Lazy WorkTitleService workTitleService) {
    this.workTitleService = workTitleService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkTitleOutput>> findAll() {
    var items = workTitleService.findAll()
        .stream().map(WorkTitleOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PostMapping()
  public ResponseEntity<WorkTitleOutput> create(@RequestBody @Valid WorkTitleInput input) {
    var entity = toEntity(input);
    var saved = workTitleService.save(entity);
    return ResponseEntity.ok(new WorkTitleOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WorkTitleOutput> findById(@PathVariable UUID id) {
    var item = workTitleService.findById(id)
        .map(WorkTitleOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PutMapping("/{id}")
  public ResponseEntity<WorkTitleOutput> update(@PathVariable UUID id, @RequestBody @Valid WorkTitleInput input) {
    if (!workTitleService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = workTitleService.save(entity);
    return ResponseEntity.ok(new WorkTitleOutput(saved));
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!workTitleService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    workTitleService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private WorkTitle toEntity(WorkTitleInput input) {
    var entity = new WorkTitle();
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
    entity.setIsOfficial(input.getIsOfficial());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<WorkTitleOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = workTitleService.findAllByWorkId(workId)
        .stream().map(WorkTitleOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}



