package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.WorkTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTagRepository extends JpaRepository<WorkTag, WorkTagId> {
}
