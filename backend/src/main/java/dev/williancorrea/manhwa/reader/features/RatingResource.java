package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.http.ResponseEntity;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.PathVariable;\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;

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
}
