package dev.williancorrea.manhwa.reader.features.volume;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VolumeRepository extends JpaRepository<Volume, UUID> {

  @Query("SELECT v FROM Volume v WHERE v.work.id = :workId and v.title = :title")
  Optional<Volume> findByWorkAndTitle(UUID workId, String title);
}

