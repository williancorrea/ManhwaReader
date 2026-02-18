package dev.williancorrea.manhwa.reader.features.scanlator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ScanlatorService {

  private final ScanlatorRepository repository;

  public ScanlatorService(@Lazy ScanlatorRepository repository) {
    this.repository = repository;
  }

  public List<Scanlator> findAll() {
    return repository.findAll();
  }

  public Optional<Scanlator> findById(UUID id) {
    return repository.findById(id);
  }

  public Scanlator save(Scanlator entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
