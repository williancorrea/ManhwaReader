package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

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
