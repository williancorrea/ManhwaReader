package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Work save(Work entity) {
    return repository.save(entity);
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
}

