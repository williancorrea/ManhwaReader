package dev.williancorrea.manhwa.reader.features.progress;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadingProgressInput {
  @NotNull
  private UUID userId;
  @NotNull
  private UUID chapterId;
  private Integer pageNumber;
  private OffsetDateTime lastReadAt;
}

