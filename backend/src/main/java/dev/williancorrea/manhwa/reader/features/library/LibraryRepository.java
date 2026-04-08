package dev.williancorrea.manhwa.reader.features.library;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibraryRepository extends JpaRepository<Library, UUID> {

  Optional<Library> findByUser_IdAndWork_Id(UUID userId, UUID workId);

  @Modifying
  @Query("DELETE FROM Library l WHERE l.user.id = :userId AND l.work.id = :workId")
  void deleteByUserIdAndWorkId(@Param("userId") UUID userId, @Param("workId") UUID workId);
}

