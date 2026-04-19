package dev.williancorrea.manhwa.reader.features.rating;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class RatingService {

  private final @Lazy RatingRepository repository;

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

  public Optional<Rating> findByUserAndWork(User user, Work work) {
    return repository.findByUser_IdAndWork_Id(user.getId(), work.getId());
  }
}

