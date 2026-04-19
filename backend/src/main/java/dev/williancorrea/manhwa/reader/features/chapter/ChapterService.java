package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class ChapterService {

  private final @Lazy ChapterRepository repository;

  public List<Chapter> findAll() {
    return repository.findAll();
  }

  public Optional<Chapter> findById(UUID id) {
    return repository.findById(id);
  }

  public Chapter save(Chapter entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Chapter> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }

  public List<Chapter> findChaptersUpTo(UUID workId, String numberFormatted, String numberVersion) {
    return repository.findChaptersUpTo(workId, numberFormatted, numberVersion);
  }

  public List<Chapter> findChaptersFrom(UUID workId, String numberFormatted, String numberVersion) {
    return repository.findChaptersFrom(workId, numberFormatted, numberVersion);
  }

  public Optional<Chapter> findByNumberAndWorkIdAndScanlatorId(String numberFormatted,
                                                               String numberVersion,
                                                               Work workId,
                                                               Scanlator scanlator,
                                                               Language language) {
    return repository.findByNumberAndWorkIdAndScanlatorIdAndLanguageId(
        numberFormatted,
        numberVersion,
        workId.getId(),
        scanlator.getId(),
        language.getId());
  }

  public Optional<Chapter> findPreviousChapter(Chapter chapter) {
    var result = repository.findPreviousChapter(
        chapter.getWork().getId(),
        chapter.getNumberFormatted(),
        chapter.getNumberVersion(),
        PageRequest.of(0, 1));
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  public Optional<Chapter> findNextChapter(Chapter chapter) {
    var result = repository.findNextChapter(
        chapter.getWork().getId(),
        chapter.getNumberFormatted(),
        chapter.getNumberVersion(),
        PageRequest.of(0, 1));
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  @Transactional(readOnly = true)
  public Page<Chapter> findPagedByWorkSlug(String slug, int page, int size, String sort, String language) {
    Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sortOrder = Sort.by(direction, "numberFormatted").and(Sort.by(direction, "numberVersion"));
    Pageable pageable = PageRequest.of(page, size, sortOrder);
    return repository.findPagedByWorkSlug(slug, language, pageable);
  }
}


