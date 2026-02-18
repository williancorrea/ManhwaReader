package dev.williancorrea.manhwa.reader.features.tag;

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
public class TagOutput implements Serializable {
  private UUID id;
  private String name;
  private Boolean isNsfw;

  public TagOutput(Tag tag) {
    this.id = tag.getId();
    this.name = tag.getName();
    this.isNsfw = tag.getIsNsfw();
  }
}
