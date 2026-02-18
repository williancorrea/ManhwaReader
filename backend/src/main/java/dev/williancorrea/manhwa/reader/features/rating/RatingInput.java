package dev.williancorrea.manhwa.reader.features.rating;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingInput {
  @NotNull
  private UUID userId;
  @NotNull
  private UUID workId;
  @NotNull
  @Min(1)
  @Max(10)
  private Integer score;
  private OffsetDateTime createdAt;
}

