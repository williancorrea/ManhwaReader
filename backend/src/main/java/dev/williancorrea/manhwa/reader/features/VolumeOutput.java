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
public class VolumeOutput implements Serializable {
  private UUID id;
  private UUID workId;
  private Integer number;
  private String title;

  public VolumeOutput(Volume volume) {
    this.id = volume.getId();
    this.workId = volume.getWork() != null ? volume.getWork().getId() : null;
    this.number = volume.getNumber();
    this.title = volume.getTitle();
  }
}
