package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.FileStorage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
}
