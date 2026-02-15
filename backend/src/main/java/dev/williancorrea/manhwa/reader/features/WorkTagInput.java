package dev.williancorrea.manhwa.reader.features;

import java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkTagInput {
  private UUID workId;
  private UUID tagId;
}
