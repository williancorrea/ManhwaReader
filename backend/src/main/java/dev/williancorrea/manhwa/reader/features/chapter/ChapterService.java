package dev.williancorrea.manhwa.reader.features.chapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class ChapterService {

  private final ChapterRepository repository;
  private final ScraperHelper scraperHelper;

  public ChapterService(@Lazy ChapterRepository repository, @Lazy ScraperHelper scraperHelper) {
    this.repository = repository;
    this.scraperHelper = scraperHelper;
  }

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

  public Optional<Chapter> findByNumberAndWorkIdAndScanlatorId(BigDecimal number,
                                                               Work workId,
                                                               Scanlator scanlator,
                                                               Language language) {
    return repository.findByNumberAndWorkIdAndScanlatorIdAndLanguageId(number, workId.getId(), scanlator.getId(),
        language.getId());
  }

  public Integer countByWorkIdAndScanlatorIdAndLanguageIdAndSynced(Work workId,
                                                                   Scanlator scanlator,
                                                                   Language language) {
    return repository.countByWorkIdAndScanlatorIdAndLanguageIdAndSynched(workId.getId(), scanlator.getId(),
        language.getId());
  }
}


