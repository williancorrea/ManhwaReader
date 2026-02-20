package dev.williancorrea.manhwa.reader.features.access.permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PermissionService {

  private final PermissionRepository repository;

  public PermissionService(@Lazy PermissionRepository repository) {
    this.repository = repository;
  }

  public List<Permission> findAll() {
    return repository.findAll();
  }

  public Optional<Permission> findById(UUID id) {
    return repository.findById(id);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }
}
