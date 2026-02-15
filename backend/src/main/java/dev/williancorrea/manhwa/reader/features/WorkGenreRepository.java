package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.WorkGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkGenreRepository extends JpaRepository<WorkGenre, WorkGenreId> {
}
