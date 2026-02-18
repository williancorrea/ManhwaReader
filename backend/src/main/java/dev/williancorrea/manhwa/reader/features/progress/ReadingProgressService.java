package dev.williancorrea.manhwa.reader.features.progress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ReadingProgressService {

  private final ReadingProgressRepository repository;

  public ReadingProgressService(@Lazy ReadingProgressRepository repository) {
    this.repository = repository;
  }

  public List<ReadingProgress> findAll() {
    return repository.findAll();
  }

  public Optional<ReadingProgress> findById(UUID id) {
    return repository.findById(id);
  }

  public ReadingProgress save(ReadingProgress entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<ReadingProgress> findAllByUserId(UUID userId) {
    return repository.findAllByUser_Id(userId);
  }
}

