package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Work;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
}
