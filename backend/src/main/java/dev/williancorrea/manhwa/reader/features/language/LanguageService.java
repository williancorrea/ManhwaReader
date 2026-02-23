package dev.williancorrea.manhwa.reader.features.language;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
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

  public Language save(Language entity) {
    return repository.save(entity);
  }

  public Optional<Language> findById(String code) {
    return repository.findByCode(code);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public Language findOrCreate(String code, SynchronizationOriginType origin) {
    return repository.findByCode(code)
        .orElseGet(() -> repository.save(Language.builder()
            .code(code)
            .name("Added by " + origin.name())
            .build()));
  }
}
