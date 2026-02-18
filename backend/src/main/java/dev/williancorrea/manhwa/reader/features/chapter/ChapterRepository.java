package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
  List<Chapter> findAllByWork_Id(UUID workId);
}

