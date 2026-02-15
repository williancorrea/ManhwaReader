package dev.williancorrea.manhwa.reader.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationGroupInput {
  private String name;
  private String description;
  private boolean active;
}
