package dev.williancorrea.manhwa.reader.system;

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
public class SystemConfigurationGroupOutput implements Serializable {
  private UUID id;
  private String name;
  private String description;
  private boolean active;

  public SystemConfigurationGroupOutput(SystemConfigurationGroup group) {
    this.id = group.getId();
    this.name = group.getName();
    this.description = group.getDescription();
    this.active = group.isActive();
  }
}
