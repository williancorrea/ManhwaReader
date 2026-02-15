package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;
import java.util.List;
import dev.williancorrea.manhwa.reader.features.WorkTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTagRepository extends JpaRepository<WorkTag, WorkTagId> {
  List<WorkTag> findAllByWork_Id(UUID workId);
}

