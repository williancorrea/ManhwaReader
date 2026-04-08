package dev.williancorrea.manhwa.reader.features.progress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, UUID> {
  List<ReadingProgress> findAllByUser_Id(UUID userId);

  Optional<ReadingProgress> findByUser_IdAndChapter_Id(UUID userId, UUID chapterId);

  @Modifying
  @Query("DELETE FROM ReadingProgress rp WHERE rp.user.id = :userId AND rp.chapter.id = :chapterId")
  void deleteByUserIdAndChapterId(@Param("userId") UUID userId, @Param("chapterId") UUID chapterId);

  @Query("SELECT rp FROM ReadingProgress rp WHERE rp.user.id = :userId AND rp.chapter.id IN :chapterIds")
  List<ReadingProgress> findAllByUserIdAndChapterIdIn(@Param("userId") UUID userId, @Param("chapterIds") List<UUID> chapterIds);
}

