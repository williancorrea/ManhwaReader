package dev.williancorrea.manhwa.reader.features;

import java.time.OffsetDateTime;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkInput {
  private String originalTitle;
  private String synopsis;
  private WorkType type;
  private WorkStatus status;
  private Integer releaseYear;
  private UUID coverImageId;
  private UUID publisherId;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
