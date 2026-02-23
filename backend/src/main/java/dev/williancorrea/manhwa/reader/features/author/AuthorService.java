package dev.williancorrea.manhwa.reader.features.author;

import java.util.Optional;
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

  public Author save(Author entity) {
    return repository.save(entity);
  }

  public Optional<Author> findByName(String name) {
    return repository.findByName(name);
  }

  public Author findOrCreate(Author author) {
    return repository.findByName(author.getName())
        .orElseGet(() -> save(author));
  }
}

