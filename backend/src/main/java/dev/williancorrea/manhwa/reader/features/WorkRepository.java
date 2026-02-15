package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.Work;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, UUID> {
  List<Work> findAllByStatus(WorkStatus status);
  List<Work> findAllByType(WorkType type);
}

