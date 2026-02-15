package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.WorkTitle;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkTitleService {

  private final WorkTitleRepository repository;

  public WorkTitleService(@Lazy WorkTitleRepository repository) {
    this.repository = repository;
  }

  public List<WorkTitle> findAll() {
    return repository.findAll();
  }

  public Optional<WorkTitle> findById(UUID id) {
    return repository.findById(id);
  }
}
