package dev.williancorrea.manhwa.reader.features;

import lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagInput {
  private String name;
  private Boolean isNsfw;
}
