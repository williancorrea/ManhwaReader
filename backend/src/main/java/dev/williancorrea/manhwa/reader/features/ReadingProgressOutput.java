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
public class ReadingProgressOutput implements Serializable {
  private UUID id;
  private UUID userId;
  private UUID chapterId;
  private Integer pageNumber;
  private OffsetDateTime lastReadAt;

  public ReadingProgressOutput(ReadingProgress readingProgress) {
    this.id = readingProgress.getId();
    this.userId = readingProgress.getUser() != null ? readingProgress.getUser().getId() : null;
    this.chapterId = readingProgress.getChapter() != null ? readingProgress.getChapter().getId() : null;
    this.pageNumber = readingProgress.getPageNumber();
    this.lastReadAt = readingProgress.getLastReadAt();
  }
}
