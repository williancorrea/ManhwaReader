package dev.williancorrea.manhwa.reader.features.work.title;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTitleRepository extends JpaRepository<WorkTitle, UUID> {
  List<WorkTitle> findAllByWork_Id(UUID workId);
}

