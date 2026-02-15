package dev.williancorrea.manhwa.reader.features;

import java.time.OffsetDateTime;\nimport java.util.UUID;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadingProgressInput {
  private UUID userId;
  private UUID chapterId;
  private Integer pageNumber;
  private OffsetDateTime lastReadAt;
}
