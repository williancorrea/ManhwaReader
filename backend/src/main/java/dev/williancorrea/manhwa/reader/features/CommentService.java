package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  public Comment save(Comment entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
  public List<Comment> findAllByWorkId(UUID workId) {
    return repository.findAllByWork_Id(workId);
  }
  public List<Comment> findAllByChapterId(UUID chapterId) {
    return repository.findAllByChapter_Id(chapterId);
  }
}


