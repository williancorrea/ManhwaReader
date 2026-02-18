package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlternativeTitleRepository extends JpaRepository<AlternativeTitle, UUID> {
  List<AlternativeTitle> findAllByWork_Id(UUID workId);
}

