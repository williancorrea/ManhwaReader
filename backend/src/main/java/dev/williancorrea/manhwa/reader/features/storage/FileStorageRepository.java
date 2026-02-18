package dev.williancorrea.manhwa.reader.features.storage;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
}
