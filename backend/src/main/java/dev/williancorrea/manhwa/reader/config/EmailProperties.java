package dev.williancorrea.manhwa.reader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

  private String from;
  private String fromName;
  private String adminEmail;
  private boolean enabled;
}
