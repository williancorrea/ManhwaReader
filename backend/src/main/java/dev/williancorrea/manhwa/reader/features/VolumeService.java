package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class VolumeService {

  private final VolumeRepository repository;

  public VolumeService(@Lazy VolumeRepository repository) {
    this.repository = repository;
  }

  public List<Volume> findAll() {
    return repository.findAll();
  }

  public Optional<Volume> findById(UUID id) {
    return repository.findById(id);
  }

  public Volume save(Volume entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
  public List<Volume> findAllByWorkId(UUID workId) {\r\n    return repository.findAllByWork_Id(workId);\r\n  }\r\n}

