package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Library;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class LibraryService {

  private final LibraryRepository repository;

  public LibraryService(@Lazy LibraryRepository repository) {
    this.repository = repository;
  }

  public List<Library> findAll() {
    return repository.findAll();
  }

  public Optional<Library> findById(UUID id) {
    return repository.findById(id);
  }
}
