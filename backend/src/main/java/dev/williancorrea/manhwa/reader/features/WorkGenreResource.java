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

  @PostMapping()
  public ResponseEntity<WorkGenreOutput> create(@RequestBody WorkGenreInput input) {
    var entity = toEntity(input);
    var saved = workGenreService.save(entity);
    return ResponseEntity.ok(new WorkGenreOutput(saved));
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

  @PutMapping("/{workId}/{genreId}")
  public ResponseEntity<WorkGenreOutput> update(@PathVariable UUID workId, @PathVariable UUID genreId, @RequestBody WorkGenreInput input) {
    var id = new WorkGenreId(workId, genreId);
    if (!workGenreService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    var work = new Work();
    work.setId(workId);
    entity.setWork(work);
    var genre = new Genre();
    genre.setId(genreId);
    entity.setGenre(genre);
    var saved = workGenreService.save(entity);
    return ResponseEntity.ok(new WorkGenreOutput(saved));
  }

  @DeleteMapping("/{workId}/{genreId}")
  public ResponseEntity<Void> delete(@PathVariable UUID workId, @PathVariable UUID genreId) {
    var id = new WorkGenreId(workId, genreId);
    if (!workGenreService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    workGenreService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private WorkGenre toEntity(WorkGenreInput input) {
    var entity = new WorkGenre();
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    if (input.getGenreId() != null) {
      var genre = new Genre();
      genre.setId(input.getGenreId());
      entity.setGenre(genre);
    }
    return entity;
  }
}

