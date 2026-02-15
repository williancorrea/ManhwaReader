package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

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
}
