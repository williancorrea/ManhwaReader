package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
