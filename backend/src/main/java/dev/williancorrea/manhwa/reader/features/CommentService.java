package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import dev.williancorrea.manhwa.reader.features.Comment;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class CommentService {

  private final CommentRepository repository;

  public CommentService(@Lazy CommentRepository repository) {
    this.repository = repository;
  }

  public List<Comment> findAll() {
    return repository.findAll();
  }

  public Optional<Comment> findById(UUID id) {
    return repository.findById(id);
  }
}
