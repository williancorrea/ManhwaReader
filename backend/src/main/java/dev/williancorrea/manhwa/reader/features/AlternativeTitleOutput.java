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
public class AlternativeTitleOutput implements Serializable {
  private UUID id;
  private UUID workId;
  private UUID languageId;
  private String title;

  public AlternativeTitleOutput(AlternativeTitle alternativeTitle) {
    this.id = alternativeTitle.getId();
    this.workId = alternativeTitle.getWork() != null ? alternativeTitle.getWork().getId() : null;
    this.languageId = alternativeTitle.getLanguage() != null ? alternativeTitle.getLanguage().getId() : null;
    this.title = alternativeTitle.getTitle();
  }
}
