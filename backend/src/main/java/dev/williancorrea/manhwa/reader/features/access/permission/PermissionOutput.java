package dev.williancorrea.manhwa.reader.features.access.permission;

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
public class PermissionOutput implements Serializable {
  private UUID id;
  private String name;

  public PermissionOutput(Permission permission) {
    this.id = permission.getId();
    this.name = permission.getName();
  }
}
