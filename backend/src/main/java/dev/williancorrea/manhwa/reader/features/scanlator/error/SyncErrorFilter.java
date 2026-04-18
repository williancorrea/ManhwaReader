package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.time.OffsetDateTime;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;

public record SyncErrorFilter(
    SynchronizationOriginType synchronization,
    String externalWorkName,
    String externalWorkId,
    String errorMessage,
    OffsetDateTime from,
    OffsetDateTime to,
    Boolean orphansOnly
) {
}
