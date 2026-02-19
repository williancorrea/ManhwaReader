package dev.williancorrea.manhwa.reader.features.page;

import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.storage.FileStorage;
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
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("features/page")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
public class PageResource {

  private final PageService pageService;

  public PageResource(@Lazy PageService pageService) {
    this.pageService = pageService;
  }

  @GetMapping()
  public ResponseEntity<List<PageOutput>> findAll() {
    var items = pageService.findAll()
        .stream().map(PageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PostMapping()
  public ResponseEntity<PageOutput> create(@RequestBody @Valid PageInput input) {
    var entity = toEntity(input);
    var saved = pageService.save(entity);
    return ResponseEntity.ok(new PageOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PageOutput> findById(@PathVariable UUID id) {
    var item = pageService.findById(id)
        .map(PageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PutMapping("/{id}")
  public ResponseEntity<PageOutput> update(@PathVariable UUID id, @RequestBody @Valid PageInput input) {
    if (!pageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = pageService.save(entity);
    return ResponseEntity.ok(new PageOutput(saved));
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!pageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    pageService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Page toEntity(PageInput input) {
    var entity = new Page();
    if (input.getChapterId() != null) {
      var chapter = new Chapter();
      chapter.setId(input.getChapterId());
      entity.setChapter(chapter);
    }
    if (input.getImageFileId() != null) {
      var file = new FileStorage();
      file.setId(input.getImageFileId());
      entity.setImageFile(file);
    }
    entity.setPageNumber(input.getPageNumber());
    return entity;
  }
  @GetMapping("/chapter/{chapterId}")
  public ResponseEntity<List<PageOutput>> findAllByChapter(@PathVariable UUID chapterId) {
    var items = pageService.findAllByChapterId(chapterId)
        .stream().map(PageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}



