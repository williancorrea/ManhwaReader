package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;

public record AdminWorkOutput(
    UUID id,
    String title,
    List<String> titles,
    String slug,
    String status,
    String type,
    String coverUrl,
    List<String> synchronizationOrigins,
    List<SynchronizationDetail> synchronizations
) {

  public record SynchronizationDetail(
      String origin,
      String externalId
  ) {}

  public static AdminWorkOutput fromEntity(Work work, String storageBase) {
    String title = null;
    if (work.getTitles() != null && !work.getTitles().isEmpty()) {
      title = work.getTitles().stream()
          .filter(t -> Boolean.TRUE.equals(t.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findFirst()
          .orElse(work.getTitles().getFirst().getTitle());
    }

    List<String> allTitles = work.getTitles() != null
        ? work.getTitles().stream()
            .map(WorkTitle::getTitle)
            .filter(t -> t != null && !t.isBlank())
            .distinct()
            .toList()
        : List.of();

    String coverUrl = work.getCoverUrl() != null
        ? storageBase + work.getCoverUrl()
        : null;

    List<String> origins = work.getSynchronizations() != null
        ? work.getSynchronizations().stream()
            .map(s -> s.getOrigin().name())
            .toList()
        : List.of();

    List<SynchronizationDetail> syncDetails = work.getSynchronizations() != null
        ? work.getSynchronizations().stream()
            .map(s -> new SynchronizationDetail(s.getOrigin().name(), s.getExternalId()))
            .toList()
        : List.of();

    return new AdminWorkOutput(
        work.getId(),
        title,
        allTitles,
        work.getSlug(),
        work.getStatus() != null ? work.getStatus().name() : null,
        work.getType() != null ? work.getType().name() : null,
        coverUrl,
        origins,
        syncDetails
    );
  }
}
