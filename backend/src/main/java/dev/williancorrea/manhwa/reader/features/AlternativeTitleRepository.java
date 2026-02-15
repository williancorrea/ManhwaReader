package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import dev.williancorrea.manhwa.reader.features.AlternativeTitle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlternativeTitleRepository extends JpaRepository<AlternativeTitle, UUID> {
  List<AlternativeTitle> findAllByWork_Id(UUID workId);
}

