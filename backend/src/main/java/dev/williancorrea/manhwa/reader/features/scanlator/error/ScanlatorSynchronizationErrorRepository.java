package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanlatorSynchronizationErrorRepository extends JpaRepository<ScanlatorSynchronizationError, UUID> {
  
}
