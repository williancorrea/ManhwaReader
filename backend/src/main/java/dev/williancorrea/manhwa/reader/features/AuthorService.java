package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Author;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class AuthorService {

  private final AuthorRepository repository;

  public AuthorService(@Lazy AuthorRepository repository) {
    this.repository = repository;
  }

  public List<Author> findAll() {
    return repository.findAll();
  }

  public Optional<Author> findById(UUID id) {
    return repository.findById(id);
  }
}
