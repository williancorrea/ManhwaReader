package dev.williancorrea.manhwa.reader.features.scanlator;

import java.math.BigDecimal;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ScanlatorService {

  private final ScanlatorRepository repository;

  public ScanlatorService(@Lazy ScanlatorRepository repository) {
    this.repository = repository;
  }

  public Optional<Scanlator> findBySynchronization(SynchronizationOriginType synchronization) {
    return repository.findBySynchronization(synchronization.name());
  }
  
  public Optional<BigDecimal> getLastChapterNumber(Work work) {
    return repository.getLastChapterNumber(work.getId());
  }
}
