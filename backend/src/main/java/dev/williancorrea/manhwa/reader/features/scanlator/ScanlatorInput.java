package dev.williancorrea.manhwa.reader.features.scanlator;

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
public class ScanlatorInput {
  @NotNull
  @Size(max = 255)
  private String name;
  @Size(max = 255)
  private String website;
}

