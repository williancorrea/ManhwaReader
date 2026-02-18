package dev.williancorrea.manhwa.reader.features.scanlator;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanlatorRepository extends JpaRepository<Scanlator, UUID> {
}
