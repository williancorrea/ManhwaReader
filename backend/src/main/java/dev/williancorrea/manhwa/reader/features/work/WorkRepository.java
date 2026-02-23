package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkRepository extends JpaRepository<Work, UUID> {
  List<Work> findAllByStatus(WorkStatus status);

  List<Work> findAllByType(WorkType type);

  @Query(value = """
      select w.* from work_synchronization ws
        inner join work w on ws.work_id = w.id
      where ws.external_id = :externalId
      """, nativeQuery = true)
  Optional<Work> findBySynchronizationExternalID(String externalId);

  @Query(value = """
      select w.* from work_title wt
        inner join work w on wt.work_id = w.id
      where  upper(wt.title) like  concat('%', upper(:title), '%')
      """
      , nativeQuery = true
  )
  Optional<Work> findByTitle(String title);
}

