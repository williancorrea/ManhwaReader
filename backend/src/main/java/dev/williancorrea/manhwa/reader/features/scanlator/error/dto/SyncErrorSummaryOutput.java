package dev.williancorrea.manhwa.reader.features.scanlator.error.dto;

import java.util.Map;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;

public record SyncErrorSummaryOutput(
    long total,
    long last24Hours,
    long last7Days,
    Map<SynchronizationOriginType, Long> byOrigin
) {
}
