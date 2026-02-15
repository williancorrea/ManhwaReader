package dev.williancorrea.manhwa.reader.features;

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
public class RatingOutput implements Serializable {
  private UUID id;
  private UUID userId;
  private UUID workId;
  private Integer score;
  private OffsetDateTime createdAt;

  public RatingOutput(Rating rating) {
    this.id = rating.getId();
    this.userId = rating.getUser() != null ? rating.getUser().getId() : null;
    this.workId = rating.getWork() != null ? rating.getWork().getId() : null;
    this.score = rating.getScore();
    this.createdAt = rating.getCreatedAt();
  }
}
