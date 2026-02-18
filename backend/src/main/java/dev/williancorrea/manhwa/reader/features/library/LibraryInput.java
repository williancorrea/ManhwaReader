package dev.williancorrea.manhwa.reader.features.library;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryInput {
  @NotNull
  private UUID userId;
  @NotNull
  private UUID workId;
  @NotNull
  private LibraryStatus status;
}

