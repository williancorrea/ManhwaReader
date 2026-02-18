package dev.williancorrea.manhwa.reader.features.scanlator;

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
public class ScanlatorOutput implements Serializable {
  private UUID id;
  private String name;
  private String website;

  public ScanlatorOutput(Scanlator scanlator) {
    this.id = scanlator.getId();
    this.name = scanlator.getName();
    this.website = scanlator.getWebsite();
  }
}
