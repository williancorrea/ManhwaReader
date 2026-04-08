package dev.williancorrea.manhwa.reader.features.chapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import lombok.RequiredArgsConstructor;
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

  private final ChapterRepository repository;

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

  @Transactional(readOnly = true)
  public Page<Chapter> findPagedByWorkSlug(String slug, int page, int size, String sort, String language) {
    Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sortOrder = Sort.by(direction, "numberFormatted").and(Sort.by(direction, "numberVersion"));
    Pageable pageable = PageRequest.of(page, size, sortOrder);
    return repository.findPagedByWorkSlug(slug, language, pageable);
  }
}


