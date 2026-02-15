package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterInput {
  @NotNull
  private UUID workId;
  private UUID volumeId;
  @NotNull
  @DecimalMin("0.01")
  private BigDecimal number;
  @Size(max = 255)
  private String title;
  @NotNull
  private UUID languageId;
  private LocalDate releaseDate;
  private UUID scanlatorId;
  private OffsetDateTime createdAt;
}

