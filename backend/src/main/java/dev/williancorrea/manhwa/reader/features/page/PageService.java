package dev.williancorrea.manhwa.reader.features.page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PageService {

  private final PageRepository repository;

  public PageService(@Lazy PageRepository repository) {
    this.repository = repository;
  }

  public List<Page> findAll() {
    return repository.findAll();
  }

  public Optional<Page> findById(UUID id) {
    return repository.findById(id);
  }

  public Page save(Page entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public List<Page> findAllByChapterId(UUID chapterId) {
    return repository.findAllByChapter_Id(chapterId);
  }
  
}

