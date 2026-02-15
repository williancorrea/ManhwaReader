package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

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
}
