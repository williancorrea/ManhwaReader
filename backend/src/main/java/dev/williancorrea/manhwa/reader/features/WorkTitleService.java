package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkTitleService {

  private final WorkTitleRepository repository;

  public WorkTitleService(@Lazy WorkTitleRepository repository) {
    this.repository = repository;
  }

  public List<WorkTitle> findAll() {
    return repository.findAll();
  }

  public Optional<WorkTitle> findById(UUID id) {
    return repository.findById(id);
  }

  public WorkTitle save(WorkTitle entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<WorkTitle> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
}

