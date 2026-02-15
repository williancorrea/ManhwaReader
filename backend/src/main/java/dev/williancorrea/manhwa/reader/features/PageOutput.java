package dev.williancorrea.manhwa.reader.features;

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
public class PageOutput implements Serializable {
  private UUID id;
  private UUID chapterId;
  private Integer pageNumber;
  private UUID imageFileId;

  public PageOutput(Page page) {
    this.id = page.getId();
    this.chapterId = page.getChapter() != null ? page.getChapter().getId() : null;
    this.pageNumber = page.getPageNumber();
    this.imageFileId = page.getImageFile() != null ? page.getImageFile().getId() : null;
  }
}
