package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class TagService {

  private final TagRepository repository;

  public TagService(@Lazy TagRepository repository) {
    this.repository = repository;
  }

  public List<Tag> findAll() {
    return repository.findAll();
  }

  public Optional<Tag> findById(UUID id) {
    return repository.findById(id);
  }

  public Tag save(Tag entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
