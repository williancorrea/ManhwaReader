package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Chapter save(Chapter entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
  public List<Chapter> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
}


