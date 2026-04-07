package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkRepository extends JpaRepository<Work, UUID>, JpaSpecificationExecutor<Work> {
  List<Work> findAllByStatus(WorkStatus status);

  List<Work> findAllByType(WorkType type);

  @Query("SELECT w FROM Work w JOIN w.synchronizations ws WHERE ws.externalId = :externalId AND ws.origin = :origin")
  Optional<Work> findBySynchronizationExternalID(String externalId, SynchronizationOriginType origin);

  @Query("SELECT w FROM Work w JOIN w.titles wt WHERE UPPER(CAST(wt.title AS string)) LIKE CONCAT('%', UPPER(:title), '%')")
  Optional<Work> findByTitle(String title);

  Optional<Work> findBySlug(String slug);
}

