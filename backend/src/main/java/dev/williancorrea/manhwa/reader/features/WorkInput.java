package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class WorkInput {
  @NotNull
  @Size(max = 255)
  private String originalTitle;
  private String synopsis;
  @NotNull
  private WorkType type;
  @NotNull
  private WorkStatus status;
  @Min(1)
  private Integer releaseYear;
  private UUID coverImageId;
  private UUID publisherId;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}

