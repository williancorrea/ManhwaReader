package dev.williancorrea.manhwa.reader.features.page;

import jakarta.validation.constraints.Min;
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
public class PageInput {
  @NotNull
  private UUID chapterId;
  @NotNull
  @Min(1)
  private Integer pageNumber;
  @NotNull
  private UUID imageFileId;
}

