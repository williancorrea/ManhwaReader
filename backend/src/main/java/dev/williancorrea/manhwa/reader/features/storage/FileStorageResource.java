package dev.williancorrea.manhwa.reader.features.storage;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/file")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','MODERATOR','UPLOADER','READER')")
public class FileStorageResource {

  private final FileStorageService fileStorageService;

  public FileStorageResource(@Lazy FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @GetMapping()
  public ResponseEntity<List<FileStorageOutput>> findAll() {
    var items = fileStorageService.findAll()
        .stream().map(FileStorageOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PostMapping()
  public ResponseEntity<FileStorageOutput> create(@RequestBody @Valid FileStorageInput input) {
    var entity = new FileStorage(input);
    var saved = fileStorageService.save(entity);
    return ResponseEntity.ok(new FileStorageOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FileStorageOutput> findById(@PathVariable UUID id) {
    var item = fileStorageService.findById(id)
        .map(FileStorageOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @PutMapping("/{id}")
  public ResponseEntity<FileStorageOutput> update(@PathVariable UUID id, @RequestBody @Valid FileStorageInput input) {
    if (!fileStorageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = new FileStorage(input);
    entity.setId(id);
    var saved = fileStorageService.save(entity);
    return ResponseEntity.ok(new FileStorageOutput(saved));
  }

  @PreAuthorize("hasAnyAuthority('ADMINISTRATOR','UPLOADER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!fileStorageService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    fileStorageService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}




