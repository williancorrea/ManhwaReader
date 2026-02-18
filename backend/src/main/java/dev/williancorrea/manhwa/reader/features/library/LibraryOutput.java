package dev.williancorrea.manhwa.reader.features.library;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryOutput implements Serializable {
  private UUID id;
  private UUID userId;
  private UUID workId;
  private LibraryStatus status;

  public LibraryOutput(Library library) {
    this.id = library.getId();
    this.userId = library.getUser() != null ? library.getUser().getId() : null;
    this.workId = library.getWork() != null ? library.getWork().getId() : null;
    this.status = library.getStatus();
  }
}
