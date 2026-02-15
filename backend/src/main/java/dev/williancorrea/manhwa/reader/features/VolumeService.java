package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Volume;
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
}
