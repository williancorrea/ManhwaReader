package dev.williancorrea.manhwa.reader.features;

import java.math.BigDecimal;\nimport java.time.LocalDate;\nimport java.time.OffsetDateTime;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterInput {
  private UUID workId;
  private UUID volumeId;
  private BigDecimal number;
  private String title;
  private UUID languageId;
  private LocalDate releaseDate;
  private UUID scanlatorId;
  private OffsetDateTime createdAt;
}
