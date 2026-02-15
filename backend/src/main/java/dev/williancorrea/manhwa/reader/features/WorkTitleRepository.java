package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.WorkTitle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTitleRepository extends JpaRepository<WorkTitle, UUID> {
  List<WorkTitle> findAllByWork_Id(UUID workId);
}

