package dev.williancorrea.manhwa.reader.features;

import java.time.OffsetDateTime;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageInput {
  private String storagePath;
  private String mimeType;
  private Long sizeBytes;
  private String checksum;
  private OffsetDateTime createdAt;
}
