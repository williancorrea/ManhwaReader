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
public class WorkTitleOutput implements Serializable {
  private UUID id;
  private UUID workId;
  private UUID languageId;
  private String title;
  private Boolean isOfficial;

  public WorkTitleOutput(WorkTitle workTitle) {
    this.id = workTitle.getId();
    this.workId = workTitle.getWork() != null ? workTitle.getWork().getId() : null;
    this.languageId = workTitle.getLanguage() != null ? workTitle.getLanguage().getId() : null;
    this.title = workTitle.getTitle();
    this.isOfficial = workTitle.getIsOfficial();
  }
}
