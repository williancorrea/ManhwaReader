package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Tag;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
