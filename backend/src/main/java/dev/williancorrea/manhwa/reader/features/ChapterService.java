package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ChapterService {

  private final ChapterRepository repository;

  public ChapterService(@Lazy ChapterRepository repository) {
    this.repository = repository;
  }

  public List<Chapter> findAll() {
    return repository.findAll();
  }

  public Optional<Chapter> findById(UUID id) {
    return repository.findById(id);
  }

  public Chapter save(Chapter entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
