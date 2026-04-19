package dev.williancorrea.manhwa.reader.features.scanlator;

import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class ScanlatorService {

  private final @Lazy ScanlatorRepository repository;

  public Optional<Scanlator> findBySynchronization(SynchronizationOriginType synchronization) {
    return repository.findBySynchronization(synchronization.name());
  }
}
