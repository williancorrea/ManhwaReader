package dev.williancorrea.manhwa.reader.features.work.dto;

import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import java.util.UUID;

public record WorkCatalogOutput(
    UUID id,
    String title,
    String coverUrl,
    String publicationDemographic,
    String status,
    Long chapterCount
) {
  public static WorkCatalogOutput fromEntity(Work work, String storage) {
    String title = null;
    if (work.getTitles() != null && !work.getTitles().isEmpty()) {
      title = work.getTitles().stream()
          .filter(t -> Boolean.TRUE.equals(t.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findFirst()
          .orElse(work.getTitles().getFirst().getTitle());
    }

    return new WorkCatalogOutput(
        work.getId(),
        title,
        storage + "/" + work.getCoverUrl(),
        work.getPublicationDemographic() != null ? work.getPublicationDemographic().name() : null,
        work.getStatus() != null ? work.getStatus().name() : null,
        work.getChapterCount() != null ? work.getChapterCount() : 0L
    );
  }
}
