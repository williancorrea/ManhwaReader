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
@RequestMapping("features/rating")
public class RatingResource {

  private final RatingService ratingService;

  public RatingResource(@Lazy RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @GetMapping()
  public ResponseEntity<List<RatingOutput>> findAll() {
    var items = ratingService.findAll()
        .stream().map(RatingOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<RatingOutput> create(@RequestBody @Valid RatingInput input) {
    var entity = toEntity(input);
    var saved = ratingService.save(entity);
    return ResponseEntity.ok(new RatingOutput(saved));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RatingOutput> findById(@PathVariable UUID id) {
    var item = ratingService.findById(id)
        .map(RatingOutput::new)
        .orElse(null);
    if (item == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(item);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RatingOutput> update(@PathVariable UUID id, @RequestBody @Valid RatingInput input) {
    if (!ratingService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    var entity = toEntity(input);
    entity.setId(id);
    var saved = ratingService.save(entity);
    return ResponseEntity.ok(new RatingOutput(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    if (!ratingService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    ratingService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private Rating toEntity(RatingInput input) {
    var entity = new Rating();
    if (input.getUserId() != null) {
      var user = new User();
      user.setId(input.getUserId());
      entity.setUser(user);
    }
    if (input.getWorkId() != null) {
      var work = new Work();
      work.setId(input.getWorkId());
      entity.setWork(work);
    }
    entity.setScore(input.getScore());
    entity.setCreatedAt(input.getCreatedAt());
    return entity;
  }
  @GetMapping("/work/{workId}")
  public ResponseEntity<List<RatingOutput>> findAllByWork(@PathVariable UUID workId) {
    var items = ratingService.findAllByWorkId(workId)
        .stream().map(RatingOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<RatingOutput>> findAllByUser(@PathVariable UUID userId) {
    var items = ratingService.findAllByUserId(userId)
        .stream().map(RatingOutput::new)
        .toList();
    return ResponseEntity.ok(items);
  }
}

