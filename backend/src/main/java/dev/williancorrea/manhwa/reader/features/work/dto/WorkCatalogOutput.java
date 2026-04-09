package dev.williancorrea.manhwa.reader.features.work.dto;

import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import java.util.UUID;

public record WorkCatalogOutput(
    UUID id,
    String slug,
    String title,
    String coverUrl,
    String publicationDemographic,
    String status,
    Long chapterCount,
    String userLibraryStatus
) {
  public static WorkCatalogOutput fromEntity(Work work, String storage) {
    return fromEntity(work, storage, null);
  }

  public static WorkCatalogOutput fromEntity(Work work, String storage, String userLibraryStatus) {
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
        work.getSlug(),
        title,
        storage + "/" + work.getCoverUrl(),
        work.getPublicationDemographic() != null ? work.getPublicationDemographic().name() : null,
        work.getStatus() != null ? work.getStatus().name() : null,
        work.getChapterCount() != null ? work.getChapterCount() : 0L,
        userLibraryStatus
    );
  }
}
