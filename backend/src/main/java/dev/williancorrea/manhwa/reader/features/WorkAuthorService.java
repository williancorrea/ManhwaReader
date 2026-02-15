package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;
import java.util.List;\nimport java.util.Optional;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class WorkAuthorService {

  private final WorkAuthorRepository repository;

  public WorkAuthorService(@Lazy WorkAuthorRepository repository) {
    this.repository = repository;
  }

  public List<WorkAuthor> findAll() {
    return repository.findAll();
  }

  public Optional<WorkAuthor> findById(WorkAuthorId id) {
    return repository.findById(id);
  }

  public WorkAuthor save(WorkAuthor entity) {
    return repository.save(entity);
  }

  public boolean existsById(WorkAuthorId id) {
    return repository.existsById(id);
  }

  public void deleteById(WorkAuthorId id) {
    repository.deleteById(id);
  }
  public List<WorkAuthor> findAllByWorkId(UUID workId) {\r\n    return repository.findAllByWork_Id(workId);\r\n  }\r\n}

