package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.ReadingProgress;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, UUID> {
  List<ReadingProgress> findAllByUser_Id(UUID userId);
}

