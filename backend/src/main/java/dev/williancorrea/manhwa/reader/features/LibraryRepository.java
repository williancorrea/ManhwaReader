package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.Library;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, UUID> {
  List<Library> findAllByUser_Id(UUID userId);
}

