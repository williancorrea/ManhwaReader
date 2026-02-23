package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  public Work save(Work entity) {
    return repository.saveAndFlush(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Work> findAllByStatus(WorkStatus status) {
    return repository.findAllByStatus(status);
  }

  public List<Work> findAllByType(WorkType type) {
    return repository.findAllByType(type);
  }


  public Optional<Work> findByTitle(String title) {
    return repository.findByTitle(title);
  }

  public Optional<Work> findBySynchronizationExternalID(String externalId) {
    return repository.findBySynchronizationExternalID(externalId);
  }

}

