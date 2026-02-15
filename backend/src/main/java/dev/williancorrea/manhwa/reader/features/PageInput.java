package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageInput {
  @NotNull
  private UUID chapterId;
  @NotNull
  private Integer pageNumber;
  @NotNull
  private UUID imageFileId;
}
