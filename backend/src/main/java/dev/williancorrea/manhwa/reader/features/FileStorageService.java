package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.FileStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class FileStorageService {

  private final FileStorageRepository repository;

  public FileStorageService(@Lazy FileStorageRepository repository) {
    this.repository = repository;
  }

  public List<FileStorage> findAll() {
    return repository.findAll();
  }

  public Optional<FileStorage> findById(UUID id) {
    return repository.findById(id);
  }
}
