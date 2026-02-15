package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkService {

  private final WorkRepository repository;

  public WorkService(@Lazy WorkRepository repository) {
    this.repository = repository;
  }

  public List<Work> findAll() {
    return repository.findAll();
  }

  public Optional<Work> findById(UUID id) {
    return repository.findById(id);
  }

  public Work save(Work entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
