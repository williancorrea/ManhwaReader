package dev.williancorrea.manhwa.reader.features.access.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class UserAccessGroupService {

  private final UserAccessGroupRepository repository;

  public UserAccessGroupService(@Lazy UserAccessGroupRepository repository) {
    this.repository = repository;
  }

  public List<UserAccessGroup> findAll() {
    return repository.findAll();
  }

  public List<UserAccessGroup> findAllByUserId(UUID userId) {
    return repository.findAllByUser_Id(userId);
  }

  public List<UserAccessGroup> findAllByAccessGroupId(UUID accessGroupId) {
    return repository.findAllByAccessGroup_Id(accessGroupId);
  }

  public Optional<UserAccessGroup> findById(UserAccessGroupId id) {
    return repository.findById(id);
  }

  public UserAccessGroup save(UserAccessGroup entity) {
    return repository.save(entity);
  }

  public boolean existsById(UserAccessGroupId id) {
    return repository.existsById(id);
  }

  public void deleteById(UserAccessGroupId id) {
    repository.deleteById(id);
  }
}
