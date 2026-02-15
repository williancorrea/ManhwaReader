package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Genre;
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
}
