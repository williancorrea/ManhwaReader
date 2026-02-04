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
public class SystemConfigurationOutput implements Serializable {

  private UUID id;
  private String group;
  private String description;
  private String reference;
  private String value;

  public SystemConfigurationOutput(SystemConfiguration systemConfiguration) {
    this.id = systemConfiguration.getId();
    this.group = systemConfiguration.getGroup().getName();
    this.description = systemConfiguration.getDescription();
    this.reference = systemConfiguration.getReference();
    this.value = systemConfiguration.getValue();
  }
}
