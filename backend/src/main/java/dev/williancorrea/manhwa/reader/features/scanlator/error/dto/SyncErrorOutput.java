package dev.williancorrea.manhwa.reader.features.scanlator.error.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationError;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;

public record SyncErrorOutput(
    UUID id,
    UUID scanlatorId,
    String scanlatorName,
    String scanlatorCode,
    String scanlatorWebsite,
    SynchronizationOriginType synchronization,
    String workId,
    String externalWorkId,
    String externalWorkName,
    String errorMessage,
    String stackTrace,
    OffsetDateTime createdAt
) {

  public static SyncErrorOutput fromEntity(ScanlatorSynchronizationError entity, boolean includeStackTrace) {
    var scanlator = entity.getScanlator();
    return new SyncErrorOutput(
        entity.getId(),
        scanlator != null ? scanlator.getId() : null,
        scanlator != null ? scanlator.getName() : null,
        scanlator != null ? scanlator.getCode() : null,
        scanlator != null ? scanlator.getWebsite() : null,
        scanlator != null ? scanlator.getSynchronization() : null,
        entity.getWorkId(),
        entity.getExternalWorkId(),
        entity.getExternalWorkName(),
        entity.getErrorMessage(),
        includeStackTrace ? entity.getStackTrace() : null,
        entity.getCreatedAt()
    );
  }
}
