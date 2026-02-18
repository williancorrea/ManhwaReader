package dev.williancorrea.manhwa.reader.features.publisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
