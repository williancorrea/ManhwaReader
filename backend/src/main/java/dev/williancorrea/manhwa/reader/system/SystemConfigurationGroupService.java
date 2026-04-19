package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class SystemConfigurationGroupService {

  private final @Lazy SystemConfigurationGroupRepository repository;

  public List<SystemConfigurationGroup> findAll() {
    return repository.findAll();
  }

  public Optional<SystemConfigurationGroup> findById(UUID id) {
    return repository.findById(id);
  }

  public SystemConfigurationGroup save(SystemConfigurationGroup entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
