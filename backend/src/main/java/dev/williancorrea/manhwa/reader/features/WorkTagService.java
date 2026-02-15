package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public WorkTag save(WorkTag entity) {
    return repository.save(entity);
  }

  public boolean existsById(WorkTagId id) {
    return repository.existsById(id);
  }

  public void deleteById(WorkTagId id) {
    repository.deleteById(id);
  }

  public List<WorkTag> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
}

