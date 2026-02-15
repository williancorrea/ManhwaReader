package dev.williancorrea.manhwa.reader.system;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationInput {
  private UUID groupId;
  private String description;
  private String reference;
  private String value;
  private boolean active;
}
