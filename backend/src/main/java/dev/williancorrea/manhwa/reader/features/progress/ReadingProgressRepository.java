package dev.williancorrea.manhwa.reader.features.progress;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, UUID> {
  List<ReadingProgress> findAllByUser_Id(UUID userId);
}

