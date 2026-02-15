package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PublisherService {

  private final PublisherRepository repository;

  public PublisherService(@Lazy PublisherRepository repository) {
    this.repository = repository;
  }

  public List<Publisher> findAll() {
    return repository.findAll();
  }

  public Optional<Publisher> findById(UUID id) {
    return repository.findById(id);
  }

  public Publisher save(Publisher entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
