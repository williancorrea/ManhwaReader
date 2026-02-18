package dev.williancorrea.manhwa.reader.features.genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class GenreService {

  private final GenreRepository repository;

  public GenreService(@Lazy GenreRepository repository) {
    this.repository = repository;
  }

  public List<Genre> findAll() {
    return repository.findAll();
  }

  public Optional<Genre> findById(UUID id) {
    return repository.findById(id);
  }

  public Genre save(Genre entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
