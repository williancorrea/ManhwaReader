package dev.williancorrea.manhwa.reader.system;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationInput {
  @NotNull
  private UUID groupId;

  @Size(max = 200)
  private String description;

  @NotNull
  @Size(max = 200)
  private String reference;

  @NotNull
  @Size(max = 200)
  private String value;

  private boolean active;
}
