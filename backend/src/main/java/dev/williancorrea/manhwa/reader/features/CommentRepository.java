package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
