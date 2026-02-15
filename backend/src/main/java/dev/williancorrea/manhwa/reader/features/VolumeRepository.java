package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Volume;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolumeRepository extends JpaRepository<Volume, UUID> {
}
