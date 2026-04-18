package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScanlatorSynchronizationErrorRepository
    extends JpaRepository<ScanlatorSynchronizationError, UUID>,
    JpaSpecificationExecutor<ScanlatorSynchronizationError> {

  long deleteByCreatedAtBefore(OffsetDateTime cutoff);

  long deleteByIdIn(List<UUID> ids);

  @Query("""
      SELECT e.scanlator.synchronization AS synchronization, COUNT(e) AS total
      FROM ScanlatorSynchronizationError e
      GROUP BY e.scanlator.synchronization
      """)
  List<SyncErrorCountByOrigin> countGroupedByOrigin();

  @Query("""
      SELECT COUNT(e)
      FROM ScanlatorSynchronizationError e
      WHERE e.createdAt >= :since
      """)
  long countSince(@Param("since") OffsetDateTime since);

  interface SyncErrorCountByOrigin {
    SynchronizationOriginType getSynchronization();
    Long getTotal();
  }
}
