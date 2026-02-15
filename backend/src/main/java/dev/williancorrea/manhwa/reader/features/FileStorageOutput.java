package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageOutput implements Serializable {
  private UUID id;
  private String storagePath;
  private String mimeType;
  private Long sizeBytes;
  private String checksum;
  private OffsetDateTime createdAt;

  public FileStorageOutput(FileStorage fileStorage) {
    this.id = fileStorage.getId();
    this.storagePath = fileStorage.getStoragePath();
    this.mimeType = fileStorage.getMimeType();
    this.sizeBytes = fileStorage.getSizeBytes();
    this.checksum = fileStorage.getChecksum();
    this.createdAt = fileStorage.getCreatedAt();
  }
}
