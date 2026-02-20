package dev.williancorrea.manhwa.reader.features.storage;

import java.time.OffsetDateTime;
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
public class FileStorageInput {
  
  @NotNull
  @Size(max = 512)
  private String storagePath;

  @Size(max = 100)
  private String mimeType;

  private Long sizeBytes;
  @Size(max = 255)
  private String checksum;

  private String fileName;

  private OffsetDateTime createdAt;
}

