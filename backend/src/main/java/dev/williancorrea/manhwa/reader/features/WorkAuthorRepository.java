package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.WorkAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkAuthorRepository extends JpaRepository<WorkAuthor, WorkAuthorId> {
}
