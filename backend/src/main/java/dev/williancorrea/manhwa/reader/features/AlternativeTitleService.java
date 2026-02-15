package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.AlternativeTitle;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class AlternativeTitleService {

  private final AlternativeTitleRepository repository;

  public AlternativeTitleService(@Lazy AlternativeTitleRepository repository) {
    this.repository = repository;
  }

  public List<AlternativeTitle> findAll() {
    return repository.findAll();
  }

  public Optional<AlternativeTitle> findById(UUID id) {
    return repository.findById(id);
  }
}
