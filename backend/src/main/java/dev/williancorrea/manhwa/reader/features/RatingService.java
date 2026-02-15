package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Rating save(Rating entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Rating> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }

  public List<Rating> findAllByUserId(UUID userId) {
    return repository.findAllByUser_Id(userId);
  }
}

