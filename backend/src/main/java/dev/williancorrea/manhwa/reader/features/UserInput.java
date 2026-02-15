package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.time.OffsetDateTime;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
  @NotNull
  @Size(max = 50)
  private String username;
  @NotNull
  @Size(max = 255)
  private String email;
  @NotNull
  @Size(max = 255)
  private String passwordHash;
  @NotNull
  @Size(max = 20)
  private String role;
  private OffsetDateTime createdAt;
}
