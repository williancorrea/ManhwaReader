package dev.williancorrea.manhwa.reader.features.comments;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutput implements Serializable {
  private UUID id;
  private UUID userId;
  private UUID workId;
  private UUID chapterId;
  private String content;
  private OffsetDateTime createdAt;

  public CommentOutput(Comment comment) {
    this.id = comment.getId();
    this.userId = comment.getUser() != null ? comment.getUser().getId() : null;
    this.workId = comment.getWork() != null ? comment.getWork().getId() : null;
    this.chapterId = comment.getChapter() != null ? comment.getChapter().getId() : null;
    this.content = comment.getContent();
    this.createdAt = comment.getCreatedAt();
  }
}
