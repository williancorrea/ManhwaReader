package dev.williancorrea.manhwa.reader.features.access.group;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessGroupPermissionInput {
  
  @NotNull
  private UUID accessGroupId;
  @NotNull
  private UUID permissionId;
}
