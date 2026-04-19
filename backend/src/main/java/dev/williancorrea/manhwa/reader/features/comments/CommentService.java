package dev.williancorrea.manhwa.reader.features.comments;

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
public class CommentService {

  private final @Lazy CommentRepository repository;

  public List<Comment> findAll() {
    return repository.findAll();
  }

  public Optional<Comment> findById(UUID id) {
    return repository.findById(id);
  }

  public Comment save(Comment entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}


