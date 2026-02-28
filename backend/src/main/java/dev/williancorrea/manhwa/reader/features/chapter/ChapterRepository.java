package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
  List<Chapter> findAllByWork_Id(UUID workId);

  @Query(nativeQuery = true,
      value = "select * from chapter where number = :number and work_id = :workId and scanlator_id")
  Optional<Chapter> findByNumberAndWorkIdAndScanlatorId(float number, UUID workId, UUID scanlatorId);
}

