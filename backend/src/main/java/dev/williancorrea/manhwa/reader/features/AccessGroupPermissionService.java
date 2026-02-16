package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class AccessGroupPermissionService {

  private final AccessGroupPermissionRepository repository;

  public AccessGroupPermissionService(@Lazy AccessGroupPermissionRepository repository) {
    this.repository = repository;
  }

  public List<AccessGroupPermission> findAll() {
    return repository.findAll();
  }

  public List<AccessGroupPermission> findAllByAccessGroupId(UUID accessGroupId) {
    return repository.findAllByAccessGroup_Id(accessGroupId);
  }

  public List<AccessGroupPermission> findAllByPermissionId(UUID permissionId) {
    return repository.findAllByPermission_Id(permissionId);
  }

  public Optional<AccessGroupPermission> findById(AccessGroupPermissionId id) {
    return repository.findById(id);
  }

  public AccessGroupPermission save(AccessGroupPermission entity) {
    return repository.save(entity);
  }

  public boolean existsById(AccessGroupPermissionId id) {
    return repository.existsById(id);
  }

  public void deleteById(AccessGroupPermissionId id) {
    repository.deleteById(id);
  }
}
