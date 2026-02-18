package dev.williancorrea.manhwa.reader.features.library;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Library save(Library entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Library> findAllByUserId(UUID userId) {
    return repository.findAllByUser_Id(userId);
  }
}

