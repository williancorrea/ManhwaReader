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
public class WorkTagOutput implements Serializable {
  private UUID workId;
  private UUID tagId;

  public WorkTagOutput(WorkTag workTag) {
    this.workId = workTag.getWork() != null ? workTag.getWork().getId() : null;
    this.tagId = workTag.getTag() != null ? workTag.getTag().getId() : null;
  }
}
