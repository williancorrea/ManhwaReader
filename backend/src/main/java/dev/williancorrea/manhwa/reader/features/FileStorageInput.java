package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport java.time.OffsetDateTime;\nimport lombok.AllArgsConstructor;\nimport lombok.Getter;\nimport lombok.NoArgsConstructor;\nimport lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageInput {
  @NotNull
  @Size(max = 512)
  private String storagePath;
  @Size(max = 100)
  private String mimeType;
  private Long sizeBytes;
  @Size(max = 255)
  private String checksum;
  private OffsetDateTime createdAt;
}
