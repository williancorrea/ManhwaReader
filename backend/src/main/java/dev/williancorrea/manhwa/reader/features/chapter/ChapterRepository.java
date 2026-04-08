package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
  List<Chapter> findAllByWork_Id(UUID workId);

  @Query("""
      SELECT c FROM Chapter c
      WHERE c.work.slug = :slug
      AND c.disabled = false
      AND (:language IS NULL OR c.language.code = :language)
      """)
  Page<Chapter> findPagedByWorkSlug(
      @Param("slug") String slug,
      @Param("language") String language,
      Pageable pageable
  );

  @Query(nativeQuery = true,
      value = """
          select * from chapter where number_formatted = :numberFormatted and number_version = :numberVersion
            and work_id = :workId
            and scanlator_id = :scanlatorId
            and language_id = :languageId
            and disabled = false
          """)
  Optional<Chapter> findByNumberAndWorkIdAndScanlatorIdAndLanguageId(String numberFormatted,
                                                                     String numberVersion,
                                                                     UUID workId,
                                                                     UUID scanlatorId,
                                                                     UUID languageId);
}

