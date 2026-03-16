package dev.williancorrea.manhwa.reader.email;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmailData {

  private String to;
  private EmailType emailType;
  private Map<String, Object> variables;
}
