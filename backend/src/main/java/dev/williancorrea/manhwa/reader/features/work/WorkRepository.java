package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.springframework.data.jpa.repository.EntityGraph;
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

  @EntityGraph(attributePaths = {"originalLanguage"})
  @Query("SELECT w FROM Work w WHERE w.slug = :slug")
  Optional<Work> findBySlugWithDetails(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.titles t LEFT JOIN FETCH t.language WHERE w.slug = :slug")
  Optional<Work> findBySlugWithTitles(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.synopses s LEFT JOIN FETCH s.language WHERE w.slug = :slug")
  Optional<Work> findBySlugWithSynopses(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.tags wt LEFT JOIN FETCH wt.tag WHERE w.slug = :slug")
  Optional<Work> findBySlugWithTags(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.authors wa LEFT JOIN FETCH wa.author WHERE w.slug = :slug")
  Optional<Work> findBySlugWithAuthors(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.links wl LEFT JOIN FETCH wl.site WHERE w.slug = :slug")
  Optional<Work> findBySlugWithLinks(@org.springframework.data.repository.query.Param("slug") String slug);

  @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.covers WHERE w.slug = :slug")
  Optional<Work> findBySlugWithCovers(@org.springframework.data.repository.query.Param("slug") String slug);
}

