package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.exception.custom.NotFoundException;
import dev.williancorrea.manhwa.reader.features.scanlator.error.dto.SyncErrorOutput;
import dev.williancorrea.manhwa.reader.features.scanlator.error.dto.SyncErrorSummaryOutput;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ScanlatorSynchronizationErrorService {

  private final ScanlatorSynchronizationErrorRepository repository;

  public ScanlatorSynchronizationErrorService(@Lazy ScanlatorSynchronizationErrorRepository repository) {
    this.repository = repository;
  }

  public ScanlatorSynchronizationError save(ScanlatorSynchronizationError entity) {
    return repository.save(entity);
  }

  @Transactional(readOnly = true)
  public Page<SyncErrorOutput> list(SyncErrorFilter filter, Pageable pageable) {
    var spec = ScanlatorSynchronizationErrorSpecification.fromFilter(filter);
    return repository.findAll(spec, pageable)
        .map(entity -> SyncErrorOutput.fromEntity(entity, false));
  }

  @Transactional(readOnly = true)
  public SyncErrorOutput findById(UUID id) {
    var entity = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("sync-error.not-found", new Object[]{id}));
    return SyncErrorOutput.fromEntity(entity, true);
  }

  @Transactional
  public void delete(UUID id) {
    if (!repository.existsById(id)) {
      throw new NotFoundException("sync-error.not-found", new Object[]{id});
    }
    repository.deleteById(id);
  }

  @Transactional
  public long deleteMany(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) return 0L;
    return repository.deleteByIdIn(ids);
  }

  @Transactional
  public long deleteOlderThan(int days) {
    var cutoff = OffsetDateTime.now().minusDays(Math.max(days, 1));
    return repository.deleteByCreatedAtBefore(cutoff);
  }

  @Transactional(readOnly = true)
  public SyncErrorSummaryOutput summary() {
    var now = OffsetDateTime.now();
    Map<SynchronizationOriginType, Long> byOrigin = new EnumMap<>(SynchronizationOriginType.class);
    for (var origin : SynchronizationOriginType.values()) {
      byOrigin.put(origin, 0L);
    }
    repository.countGroupedByOrigin().forEach(row -> {
      if (row.getSynchronization() != null) {
        byOrigin.put(row.getSynchronization(), row.getTotal());
      }
    });

    long total = byOrigin.values().stream().mapToLong(Long::longValue).sum();
    long last24h = repository.countSince(now.minus(24, ChronoUnit.HOURS));
    long last7d = repository.countSince(now.minus(7, ChronoUnit.DAYS));

    return new SyncErrorSummaryOutput(total, last24h, last7d, byOrigin);
  }
}
