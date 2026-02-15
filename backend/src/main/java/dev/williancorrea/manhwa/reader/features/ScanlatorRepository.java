package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Scanlator;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanlatorRepository extends JpaRepository<Scanlator, UUID> {
}
