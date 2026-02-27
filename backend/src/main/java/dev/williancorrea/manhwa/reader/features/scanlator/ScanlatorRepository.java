package dev.williancorrea.manhwa.reader.features.scanlator;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScanlatorRepository extends JpaRepository<Scanlator, UUID> {

  @Query(nativeQuery = true,
      value = "select s.* from scanlator s where s.synchronization = :synchronization")
  Optional<Scanlator> findBySynchronization(String synchronization);

  @Query(nativeQuery = true,
      value = "select max(c.number) from chapter c where c.work_id = :workId")
  Optional<BigDecimal> getLastChapterNumber(UUID workId);
}
