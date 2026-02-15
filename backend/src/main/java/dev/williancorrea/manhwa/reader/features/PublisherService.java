package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Publisher;
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
}
