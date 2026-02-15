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
@RequestMapping("features/volume")
public class VolumeResource {

  private final VolumeService volumeService;

  public VolumeResource(@Lazy VolumeService volumeService) {
    this.volumeService = volumeService;
  }

  @GetMapping()
  public ResponseEntity<List<VolumeOutput>> findAll() {
    var items = volumeService.findAll()
        .stream().map(VolumeOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<VolumeOutput> create(@RequestBody @Valid VolumeInput input) {
    var entity = toEntity(input);
    var saved = volumeService.save(entity);
    return ResponseEntity.ok(new VolumeOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<VolumeOutput> findById(@PathVariable UUID id) {
    var item = volumeService.findById(id)
        .map(VolumeOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<VolumeOutput> update(@PathVariable UUID id, @RequestBody @Valid VolumeInput input) {
    if (!volumeService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = volumeService.save(entity);
    return ResponseEntity.ok(new VolumeOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!volumeService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    volumeService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Volume toEntity(VolumeInput input) {
    var entity = new Volume();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    entity.setNumber(input.getNumber());
    entity.setTitle(input.getTitle());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<VolumeOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = volumeService.findAllByWorkId(workId)
        .stream().map(VolumeOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

