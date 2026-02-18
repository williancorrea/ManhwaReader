package dev.williancorrea.manhwa.reader.features.library;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, UUID> {
  List<Library> findAllByUser_Id(UUID userId);
}

