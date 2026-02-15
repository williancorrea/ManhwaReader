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
@RequestMapping("features/page")
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

  @PostMapping()
  public ResponseEntity<PageOutput> create(@RequestBody PageInput input) {
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

  @PutMapping("/{id}")
  public ResponseEntity<PageOutput> update(@PathVariable UUID id, @RequestBody PageInput input) {
    if (!pageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = pageService.save(entity);
    return ResponseEntity.ok(new PageOutput(saved));
  }

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
}

