package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.ReadingProgress;
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
}
