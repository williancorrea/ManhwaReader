package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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

  public List<Volume> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
}