package dev.williancorrea.manhwa.reader.features.comments;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
  List<Comment> findAllByWork_Id(UUID workId);
  List<Comment> findAllByChapter_Id(UUID chapterId);
}

