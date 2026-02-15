package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkTitleInput {
  @NotNull
  private UUID workId;
  @NotNull
  private UUID languageId;
  @NotNull
  @Size(max = 255)
  private String title;
  private Boolean isOfficial;
}
