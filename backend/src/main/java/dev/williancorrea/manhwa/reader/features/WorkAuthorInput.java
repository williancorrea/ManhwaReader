package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkAuthorInput {
  @NotNull
  private UUID workId;
  @NotNull
  private UUID authorId;
  @Size(max = 50)
  private String role;
}
