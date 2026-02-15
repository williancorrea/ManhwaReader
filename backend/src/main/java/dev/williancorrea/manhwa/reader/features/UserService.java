package dev.williancorrea.manhwa.reader.features;

import java.util.List;\nimport java.util.Optional;\nimport java.util.UUID;\nimport org.springframework.context.annotation.Lazy;\nimport org.springframework.stereotype.Service;\nimport org.springframework.validation.annotation.Validated;

@Validated
@Service
public class UserService {

  private final UserRepository repository;

  public UserService(@Lazy UserRepository repository) {
    this.repository = repository;
  }

  public List<User> findAll() {
    return repository.findAll();
  }

  public Optional<User> findById(UUID id) {
    return repository.findById(id);
  }

  public User save(User entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
