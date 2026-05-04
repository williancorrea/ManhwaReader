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

  @Query("SELECT l.work.id, l.status FROM Library l WHERE l.user.id = :userId AND l.work.id IN :workIds")
  List<Object[]> findWorkIdAndStatusByUserIdAndWorkIdIn(
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
        COALESCE((
          SELECT MAX(CAST(c.number AS DECIMAL))
          FROM chapter c
          WHERE c.work_id = w.id
        ), 0) AS chapter_count,
        COALESCE(MAX(CASE WHEN wt.is_official = 1 THEN wt.title END), MAX(wt.title)) AS title,
        COALESCE(MAX(CASE WHEN wc.is_official = 1 THEN wc.file_name END), MAX(wc.file_name)) AS cover_file_name,
        (
          SELECT COUNT(c2.id)
          FROM chapter c2
          LEFT JOIN reading_progress rp2 ON rp2.chapter_id = c2.id AND rp2.user_id = :userId
          WHERE c2.work_id = w.id AND c2.disabled = FALSE AND rp2.id IS NULL
        ) AS unread_count,
        lang.code AS original_language_code,
        lang.flag AS original_language_flag,
        lang.name AS original_language_name,
        (
          SELECT MAX(rp.last_read_at)
          FROM reading_progress rp
          JOIN chapter ch ON ch.id = rp.chapter_id
          WHERE ch.work_id = w.id AND rp.user_id = :userId
        ) AS last_read_at
      FROM library l
      JOIN work w ON l.work_id = w.id
      LEFT JOIN language lang ON lang.id = w.original_language_id
      LEFT JOIN work_title wt ON wt.work_id = w.id
      LEFT JOIN work_cover wc ON wc.work_id = w.id
      WHERE l.user_id = :userId
      GROUP BY l.id, w.id, w.slug, l.status, w.publication_demographic, w.status, lang.code, lang.flag, lang.name
      HAVING unread_count > 0
      ORDER BY last_read_at DESC
      LIMIT :limit
      """)
  List<Object[]> findContinueReadingByUserId(
      @Param("userId") UUID userId,
      @Param("limit") int limit
  );

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
           AND NOT EXISTS (
             SELECT 1 FROM reading_progress rp WHERE rp.chapter_id = c2.id AND rp.user_id = :userId
           )
          ), 0
        ) AS unread_count,
        lang.code AS original_language_code,
        lang.flag AS original_language_flag,
        lang.name AS original_language_name
      FROM library l
      JOIN work w ON l.work_id = w.id
      LEFT JOIN language lang ON lang.id = w.original_language_id
      WHERE l.user_id = :userId
        AND (:status IS NULL OR l.status = :status)
        AND (:title IS NULL OR EXISTS (
          SELECT 1 FROM work_title wt2 WHERE wt2.work_id = w.id AND LOWER(wt2.title) LIKE LOWER(CONCAT('%', :title, '%'))
        ))
      ORDER BY unread_count DESC
      """,
      countQuery = """
      SELECT COUNT(l.id)
      FROM library l
      JOIN work w ON l.work_id = w.id
      WHERE l.user_id = :userId
        AND (:status IS NULL OR l.status = :status)
        AND (:title IS NULL OR EXISTS (
          SELECT 1 FROM work_title wt2 WHERE wt2.work_id = w.id AND LOWER(wt2.title) LIKE LOWER(CONCAT('%', :title, '%'))
        ))
      """)
  Page<Object[]> findLibraryItemsByUserId(
      @Param("userId") UUID userId,
      @Param("status") String status,
      @Param("title") String title,
      Pageable pageable
  );
}

