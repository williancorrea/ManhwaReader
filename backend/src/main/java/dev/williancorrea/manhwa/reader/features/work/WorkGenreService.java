package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkGenreService {

  private final WorkGenreRepository repository;

  public WorkGenreService(@Lazy WorkGenreRepository repository) {
    this.repository = repository;
  }

  public List<WorkGenre> findAll() {
    return repository.findAll();
  }

  public Optional<WorkGenre> findById(WorkGenreId id) {
    return repository.findById(id);
  }

  public WorkGenre save(WorkGenre entity) {
    return repository.save(entity);
  }

  public boolean existsById(WorkGenreId id) {
    return repository.existsById(id);
  }

  public void deleteById(WorkGenreId id) {
    repository.deleteById(id);
  }

  public List<WorkGenre> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
}

