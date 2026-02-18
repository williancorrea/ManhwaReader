package dev.williancorrea.manhwa.reader.features.volume;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolumeRepository extends JpaRepository<Volume, UUID> {
  List<Volume> findAllByWork_Id(UUID workId);
}

