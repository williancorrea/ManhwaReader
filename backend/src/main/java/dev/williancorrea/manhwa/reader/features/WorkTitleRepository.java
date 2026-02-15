package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.WorkTitle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTitleRepository extends JpaRepository<WorkTitle, UUID> {
}
