package dev.williancorrea.manhwa.reader.features.library;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibraryRepository extends JpaRepository<Library, UUID> {

  Optional<Library> findByUser_IdAndWork_Id(UUID userId, UUID workId);

  @Query("SELECT l FROM Library l WHERE l.user.id = :userId AND l.work.id IN :workIds")
  List<Library> findByUserIdAndWorkIdIn(
      @Param("userId") UUID userId,
      @Param("workIds") List<UUID> workIds
  );

  @Modifying
  @Query("DELETE FROM Library l WHERE l.user.id = :userId AND l.work.id = :workId")
  void deleteByUserIdAndWorkId(@Param("userId") UUID userId, @Param("workId") UUID workId);

  @Query(nativeQuery = true, value = """
      SELECT
        l.id AS id,
        w.id AS work_id,
        w.slug AS slug,
        l.status AS library_status,
        w.publication_demographic AS publication_demographic,
        w.status AS work_status,
        COALESCE((SELECT MAX(CAST(c.number AS DECIMAL)) FROM chapter c WHERE c.work_id = w.id), 0) AS chapter_count,
        COALESCE(
          (SELECT wt.title FROM work_title wt WHERE wt.work_id = w.id AND wt.is_official = 1 LIMIT 1),
          (SELECT wt.title FROM work_title wt WHERE wt.work_id = w.id LIMIT 1)
        ) AS title,
        COALESCE(
          (SELECT wc.file_name FROM work_cover wc WHERE wc.work_id = w.id AND wc.is_official = 1 LIMIT 1),
          (SELECT wc.file_name FROM work_cover wc WHERE wc.work_id = w.id LIMIT 1)
        ) AS cover_file_name,
        COALESCE(
          (SELECT COUNT(c2.id) FROM chapter c2
           WHERE c2.work_id = w.id AND c2.disabled = false
           AND c2.id NOT IN (SELECT rp.chapter_id FROM reading_progress rp WHERE rp.user_id = :userId)
          ), 0
        ) AS unread_count
      FROM library l
      JOIN work w ON l.work_id = w.id
      WHERE l.user_id = :userId
        AND (:status IS NULL OR l.status = :status)
      ORDER BY unread_count DESC
      """,
      countQuery = """
      SELECT COUNT(l.id)
      FROM library l
      WHERE l.user_id = :userId
        AND (:status IS NULL OR l.status = :status)
      """)
  Page<Object[]> findLibraryItemsByUserId(
      @Param("userId") UUID userId,
      @Param("status") String status,
      Pageable pageable
  );
}

