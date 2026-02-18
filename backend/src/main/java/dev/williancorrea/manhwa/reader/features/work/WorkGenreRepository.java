package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkGenreRepository extends JpaRepository<WorkGenre, WorkGenreId> {
  List<WorkGenre> findAllByWork_Id(UUID workId);
}

