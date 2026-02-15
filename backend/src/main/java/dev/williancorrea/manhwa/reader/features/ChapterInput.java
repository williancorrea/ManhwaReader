package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.math.BigDecimal;\nimport java.time.LocalDate;\nimport java.time.OffsetDateTime;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterInput {
  @NotNull
  private UUID workId;
  private UUID volumeId;
  @NotNull
  private BigDecimal number;
  @Size(max = 255)
  private String title;
  @NotNull
  private UUID languageId;
  private LocalDate releaseDate;
  private UUID scanlatorId;
  private OffsetDateTime createdAt;
}
