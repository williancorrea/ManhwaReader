package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.Volume;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolumeRepository extends JpaRepository<Volume, UUID> {
  List<Volume> findAllByWork_Id(UUID workId);
}

