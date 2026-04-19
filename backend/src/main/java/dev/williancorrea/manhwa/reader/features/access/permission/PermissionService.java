package dev.williancorrea.manhwa.reader.features.access.permission;

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
public class PermissionService {

  private final @Lazy PermissionRepository repository;

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
