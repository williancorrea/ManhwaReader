package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Page;
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
}
