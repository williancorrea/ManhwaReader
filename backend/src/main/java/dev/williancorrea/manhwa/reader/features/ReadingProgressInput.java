package dev.williancorrea.manhwa.reader.features;

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
public class ReadingProgressInput {
  @NotNull
  private UUID userId;
  @NotNull
  private UUID chapterId;
  private Integer pageNumber;
  private OffsetDateTime lastReadAt;
}

