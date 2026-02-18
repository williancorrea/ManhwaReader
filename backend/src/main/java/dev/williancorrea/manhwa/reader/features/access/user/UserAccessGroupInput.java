package dev.williancorrea.manhwa.reader.features.access.user;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessGroupInput {
  @NotNull
  private UUID userId;
  @NotNull
  private UUID accessGroupId;
}
