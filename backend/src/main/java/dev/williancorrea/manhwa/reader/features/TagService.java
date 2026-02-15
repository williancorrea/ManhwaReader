package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Tag;
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
}
