package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkAuthorOutput implements Serializable {
  private UUID workId;
  private UUID authorId;
  private String role;

  public WorkAuthorOutput(WorkAuthor workAuthor) {
    this.workId = workAuthor.getWork() != null ? workAuthor.getWork().getId() : null;
    this.authorId = workAuthor.getAuthor() != null ? workAuthor.getAuthor().getId() : null;
    this.role = workAuthor.getRole();
  }
}
