package dev.williancorrea.manhwa.reader.system;

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
public class SystemConfigurationGroupInput {
  @NotNull
  @Size(max = 200)
  private String name;

  @Size(max = 200)
  private String description;

  private boolean active;
}
