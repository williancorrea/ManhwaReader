package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.WorkTag;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkTagService {

  private final WorkTagRepository repository;

  public WorkTagService(@Lazy WorkTagRepository repository) {
    this.repository = repository;
  }

  public List<WorkTag> findAll() {
    return repository.findAll();
  }

  public Optional<WorkTag> findById(WorkTagId id) {
    return repository.findById(id);
  }
}
