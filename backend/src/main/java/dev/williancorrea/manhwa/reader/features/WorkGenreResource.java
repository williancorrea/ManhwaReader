package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("features/work-genre")
public class WorkGenreResource {

  private final WorkGenreService workGenreService;

  public WorkGenreResource(@Lazy WorkGenreService workGenreService) {
    this.workGenreService = workGenreService;
  }

  @GetMapping()
  public ResponseEntity<List<WorkGenreOutput>> findAll() {
    var items = workGenreService.findAll()
        .stream().map(WorkGenreOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{workId}/{genreId}")
  public ResponseEntity<WorkGenreOutput> findById(@PathVariable UUID workId, @PathVariable UUID genreId) {
    var id = new WorkGenreId(workId, genreId);
    var item = workGenreService.findById(id)
        .map(WorkGenreOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }
}
