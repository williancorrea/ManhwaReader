package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Chapter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ChapterService {

  private final ChapterRepository repository;

  public ChapterService(@Lazy ChapterRepository repository) {
    this.repository = repository;
  }

  public List<Chapter> findAll() {
    return repository.findAll();
  }

  public Optional<Chapter> findById(UUID id) {
    return repository.findById(id);
  }
}
