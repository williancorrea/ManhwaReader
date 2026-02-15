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
public class WorkOutput implements Serializable {
  private UUID id;
  private String originalTitle;
  private String synopsis;
  private WorkType type;
  private WorkStatus status;
  private Integer releaseYear;
  private UUID coverImageId;
  private UUID publisherId;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public WorkOutput(Work work) {
    this.id = work.getId();
    this.originalTitle = work.getOriginalTitle();
    this.synopsis = work.getSynopsis();
    this.type = work.getType();
    this.status = work.getStatus();
    this.releaseYear = work.getReleaseYear();
    this.coverImageId = work.getCoverImage() != null ? work.getCoverImage().getId() : null;
    this.publisherId = work.getPublisher() != null ? work.getPublisher().getId() : null;
    this.createdAt = work.getCreatedAt();
    this.updatedAt = work.getUpdatedAt();
  }
}
