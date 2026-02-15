package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.time.OffsetDateTime;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkInput {
  @NotNull
  @Size(max = 255)
  private String originalTitle;
  private String synopsis;
  @NotNull
  private WorkType type;
  @NotNull
  private WorkStatus status;
  private Integer releaseYear;
  private UUID coverImageId;
  private UUID publisherId;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
