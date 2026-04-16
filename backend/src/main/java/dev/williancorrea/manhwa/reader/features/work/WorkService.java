package dev.williancorrea.manhwa.reader.features.work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkService {

  private final WorkRepository repository;
  private final ScraperHelper scraperHelper;

  public WorkService(@Lazy WorkRepository repository, @Lazy ScraperHelper scraperHelper) {
    this.repository = repository;
    this.scraperHelper = scraperHelper;
  }

  public List<Work> findAll() {
    return repository.findAll();
  }

  public Optional<Work> findById(UUID id) {
    return repository.findById(id);
  }

  @Transactional
  public Work saveAndNotifyIfNew(Work entity, SynchronizationOriginType origin) {
    boolean isNew = entity.getId() == null;
    Work savedWork = repository.saveAndFlush(entity);

    if (isNew) {
      scraperHelper.notifyWorkAdded(savedWork, origin);
    }
    return savedWork;
  }

  @Transactional
  public Work save(Work entity) {
    return repository.saveAndFlush(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Work> findAllByStatus(WorkStatus status) {
    return repository.findAllByStatus(status);
  }

  public List<Work> findAllByType(WorkType type) {
    return repository.findAllByType(type);
  }


  public Optional<Work> findByTitle(String title) {
    return repository.findByTitle(title);
  }

  public Optional<Work> findBySynchronizationExternalID(String externalId, SynchronizationOriginType origin) {
    return repository.findBySynchronizationExternalID(externalId, origin);
  }

  public Optional<Work> findBySlug(String slug) {
    return repository.findBySlug(slug);
  }

  @Transactional(readOnly = true)
  public Optional<Work> findBySlugWithDetails(String slug) {
    return repository.findBySlugWithDetails(slug);
  }

  @Transactional(readOnly = true)
  public Page<Work> findAll(Specification<Work> spec, Pageable pageable) {
    return repository.findAll(spec, pageable);
  }

  @Transactional(readOnly = true)
  public Page<Work> findAllWorks(WorkCatalogFilter filter, Pageable pageable) {
    Sort sort = resolveSort(filter.sort());
    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    return repository.findAll(WorkSpecification.fromFilter(filter), sortedPageable);
  }

  private Sort resolveSort(String sortParam) {
    if (sortParam == null) return Sort.by(Sort.Direction.DESC, "updatedAt");
    return switch (sortParam) {
      case "updated_at_asc" -> Sort.by(Sort.Direction.ASC, "updatedAt");
      default -> Sort.by(Sort.Direction.DESC, "updatedAt");
    };
  }

}

