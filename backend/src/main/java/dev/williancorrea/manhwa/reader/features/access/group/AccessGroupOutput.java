package dev.williancorrea.manhwa.reader.features.access.group;

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
public class AccessGroupOutput implements Serializable {
  private UUID id;
  private String name;

  public AccessGroupOutput(AccessGroup accessGroup) {
    this.id = accessGroup.getId();
    this.name = accessGroup.getName().name();
  }
}
