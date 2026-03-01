package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
  List<Chapter> findAllByWork_Id(UUID workId);

  @Query(nativeQuery = true,
      value = """
          select * from chapter where number = :number
            and work_id = :workId
            and scanlator_id = :scanlatorId
            and language_id = :languageId
          """)
  Optional<Chapter> findByNumberAndWorkIdAndScanlatorIdAndLanguageId(float number, UUID workId, UUID scanlatorId,
                                                                     UUID languageId);

  @Query(nativeQuery = true,
      value = """
          select count(*) from chapter where work_id = :workId
            and scanlator_id = :scanlatorId
            and language_id = :languageId
          """)
  Integer countByWorkIdAndScanlatorIdAndLanguageId(UUID workId, UUID scanlatorId, UUID languageId);
}

