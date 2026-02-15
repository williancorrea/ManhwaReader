package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VolumeInput {
  private UUID workId;
  private Integer number;
  private String title;
}
