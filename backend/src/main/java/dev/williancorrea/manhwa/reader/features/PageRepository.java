package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.Page;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, UUID> {
  List<Page> findAllByChapter_Id(UUID chapterId);
}

