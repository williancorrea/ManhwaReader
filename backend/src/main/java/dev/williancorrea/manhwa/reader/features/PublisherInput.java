package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublisherInput {
  @NotNull
  @Size(max = 255)
  private String name;
}
