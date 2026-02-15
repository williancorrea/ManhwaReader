package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;
import java.util.List;
import dev.williancorrea.manhwa.reader.features.WorkAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkAuthorRepository extends JpaRepository<WorkAuthor, WorkAuthorId> {
  List<WorkAuthor> findAllByWork_Id(UUID workId);
}

