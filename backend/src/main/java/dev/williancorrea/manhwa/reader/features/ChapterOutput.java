package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ChapterOutput implements Serializable {
  private UUID id;
  private UUID workId;
  private UUID volumeId;
  private BigDecimal number;
  private String title;
  private UUID languageId;
  private LocalDate releaseDate;
  private UUID scanlatorId;
  private OffsetDateTime createdAt;

  public ChapterOutput(Chapter chapter) {
    this.id = chapter.getId();
    this.workId = chapter.getWork() != null ? chapter.getWork().getId() : null;
    this.volumeId = chapter.getVolume() != null ? chapter.getVolume().getId() : null;
    this.number = chapter.getNumber();
    this.title = chapter.getTitle();
    this.languageId = chapter.getLanguage() != null ? chapter.getLanguage().getId() : null;
    this.releaseDate = chapter.getReleaseDate();
    this.scanlatorId = chapter.getScanlator() != null ? chapter.getScanlator().getId() : null;
    this.createdAt = chapter.getCreatedAt();
  }
}
