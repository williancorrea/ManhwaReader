package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTagRepository extends JpaRepository<WorkTag, WorkTagId> {
  List<WorkTag> findAllByWork_Id(UUID workId);
}

