package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Language;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class LanguageService {

  private final LanguageRepository repository;

  public LanguageService(@Lazy LanguageRepository repository) {
    this.repository = repository;
  }

  public List<Language> findAll() {
    return repository.findAll();
  }

  public Optional<Language> findById(UUID id) {
    return repository.findById(id);
  }
}
