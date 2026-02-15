package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.WorkGenre;
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
}
