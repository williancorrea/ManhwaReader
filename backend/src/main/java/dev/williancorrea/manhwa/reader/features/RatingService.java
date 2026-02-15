package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Rating;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class RatingService {

  private final RatingRepository repository;

  public RatingService(@Lazy RatingRepository repository) {
    this.repository = repository;
  }

  public List<Rating> findAll() {
    return repository.findAll();
  }

  public Optional<Rating> findById(UUID id) {
    return repository.findById(id);
  }
}
