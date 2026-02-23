package dev.williancorrea.manhwa.reader.features.work.link;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkLinkRepository extends JpaRepository<WorkLink, UUID> {
  
  @Query(value = """
      select s.* from site s where s.code = :code
      """, nativeQuery = true)
  Optional<Site> findBySiteCode(String code);
}

