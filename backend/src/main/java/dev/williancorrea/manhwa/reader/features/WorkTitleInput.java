package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkTitleInput {
  @NotNull
  private UUID workId;
  @NotNull
  private UUID languageId;
  @NotNull
  @Size(max = 255)
  private String title;
  private Boolean isOfficial;
}

