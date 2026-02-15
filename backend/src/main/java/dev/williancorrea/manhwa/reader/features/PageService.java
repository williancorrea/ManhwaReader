package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PageService {

  private final PageRepository repository;

  public PageService(@Lazy PageRepository repository) {
    this.repository = repository;
  }

  public List<Page> findAll() {
    return repository.findAll();
  }

  public Optional<Page> findById(UUID id) {
    return repository.findById(id);
  }

  public Page save(Page entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
