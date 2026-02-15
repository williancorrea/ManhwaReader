package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;
import java.util.List;
import dev.williancorrea.manhwa.reader.features.WorkGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkGenreRepository extends JpaRepository<WorkGenre, WorkGenreId> {
  List<WorkGenre> findAllByWork_Id(UUID workId);
}

