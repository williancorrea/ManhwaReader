package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.Min;
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
public class VolumeInput {
  @NotNull
  private UUID workId;
  @NotNull
  @Min(1)
  private Integer number;
  @Size(max = 255)
  private String title;
}

