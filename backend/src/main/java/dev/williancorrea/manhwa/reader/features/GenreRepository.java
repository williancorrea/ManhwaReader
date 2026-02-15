package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Genre;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
}
