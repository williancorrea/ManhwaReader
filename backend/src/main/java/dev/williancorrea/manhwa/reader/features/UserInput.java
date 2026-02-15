package dev.williancorrea.manhwa.reader.features;

import java.time.OffsetDateTime;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
  private String username;
  private String email;
  private String passwordHash;
  private String role;
  private OffsetDateTime createdAt;
}
