package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

