package dev.williancorrea.manhwa.reader.features.work;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkAuthorInput {
  @NotNull
  private UUID workId;
  @NotNull
  private UUID authorId;
  @Size(max = 50)
  private String role;
}

