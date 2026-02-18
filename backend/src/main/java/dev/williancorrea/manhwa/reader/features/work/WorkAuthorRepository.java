package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkAuthorRepository extends JpaRepository<WorkAuthor, WorkAuthorId> {
  List<WorkAuthor> findAllByWork_Id(UUID workId);
}

