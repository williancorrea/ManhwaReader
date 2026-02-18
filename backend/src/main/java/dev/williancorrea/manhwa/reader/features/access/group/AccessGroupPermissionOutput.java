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
public class AccessGroupPermissionOutput implements Serializable {
  private UUID accessGroupId;
  private UUID permissionId;

  public AccessGroupPermissionOutput(AccessGroupPermission accessGroupPermission) {
    this.accessGroupId = accessGroupPermission.getAccessGroup() != null ? accessGroupPermission.getAccessGroup().getId() : null;
    this.permissionId = accessGroupPermission.getPermission() != null ? accessGroupPermission.getPermission().getId() : null;
  }
}
