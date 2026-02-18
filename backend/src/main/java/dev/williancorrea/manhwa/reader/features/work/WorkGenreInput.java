package dev.williancorrea.manhwa.reader.features.work;

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
public class WorkGenreInput {
  @NotNull
  private UUID workId;
  @NotNull
  private UUID genreId;
}

