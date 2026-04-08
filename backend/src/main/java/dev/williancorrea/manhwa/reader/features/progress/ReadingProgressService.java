package dev.williancorrea.manhwa.reader.features.progress;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ReadingProgressService {

  private final ReadingProgressRepository repository;

  public ReadingProgressService(@Lazy ReadingProgressRepository repository) {
    this.repository = repository;
  }

  public List<ReadingProgress> findAll() {
    return repository.findAll();
  }

  public Optional<ReadingProgress> findById(UUID id) {
    return repository.findById(id);
  }

  public ReadingProgress save(ReadingProgress entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public Optional<ReadingProgress> findByUserAndChapter(User user, Chapter chapter) {
    return repository.findByUser_IdAndChapter_Id(user.getId(), chapter.getId());
  }

  @Transactional
  public ReadingProgress saveOrUpdate(User user, Chapter chapter) {
    ReadingProgress progress = repository.findByUser_IdAndChapter_Id(user.getId(), chapter.getId())
        .orElse(ReadingProgress.builder().user(user).chapter(chapter).build());
    progress.setLastReadAt(OffsetDateTime.now());
    return repository.save(progress);
  }

  @Transactional
  public void deleteByUserAndChapter(User user, Chapter chapter) {
    repository.deleteByUserIdAndChapterId(user.getId(), chapter.getId());
  }

  @Transactional
  public void markAllAsRead(User user, List<Chapter> chapters) {
    for (Chapter chapter : chapters) {
      if (repository.findByUser_IdAndChapter_Id(user.getId(), chapter.getId()).isEmpty()) {
        repository.save(ReadingProgress.builder()
            .user(user)
            .chapter(chapter)
            .lastReadAt(OffsetDateTime.now())
            .build());
      }
    }
  }

  @Transactional
  public void unmarkAllByWorkId(User user, UUID workId) {
    repository.deleteAllByUserIdAndWorkId(user.getId(), workId);
  }

  public Map<UUID, ReadingProgress> findAllByUserAndChapterIds(User user, List<UUID> chapterIds) {
    if (chapterIds == null || chapterIds.isEmpty()) return Map.of();
    return repository.findAllByUserIdAndChapterIdIn(user.getId(), chapterIds).stream()
        .collect(Collectors.toMap(rp -> rp.getChapter().getId(), rp -> rp));
  }
}

