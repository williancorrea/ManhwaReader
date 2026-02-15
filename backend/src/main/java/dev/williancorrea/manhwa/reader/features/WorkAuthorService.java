package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.WorkAuthor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkAuthorService {

  private final WorkAuthorRepository repository;

  public WorkAuthorService(@Lazy WorkAuthorRepository repository) {
    this.repository = repository;
  }

  public List<WorkAuthor> findAll() {
    return repository.findAll();
  }

  public Optional<WorkAuthor> findById(WorkAuthorId id) {
    return repository.findById(id);
  }
}
