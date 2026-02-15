package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.ReadingProgress;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, UUID> {
}
