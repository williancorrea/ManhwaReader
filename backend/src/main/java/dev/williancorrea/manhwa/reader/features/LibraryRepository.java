package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Library;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, UUID> {
}
