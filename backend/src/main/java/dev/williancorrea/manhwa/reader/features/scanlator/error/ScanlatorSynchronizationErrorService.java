package dev.williancorrea.manhwa.reader.features.scanlator.error;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ScanlatorSynchronizationErrorService {

  private final ScanlatorSynchronizationErrorRepository repository;

  public ScanlatorSynchronizationErrorService(@Lazy ScanlatorSynchronizationErrorRepository repository) {
    this.repository = repository;
  }

  public ScanlatorSynchronizationError save(ScanlatorSynchronizationError entity) {
    return repository.save(entity);
  }

}
