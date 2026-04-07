package dev.williancorrea.manhwa.reader.features.work.dto;

import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkType;

public record WorkCatalogFilter(
    String title,
    WorkType type,
    WorkPublicationDemographic publicationDemographic,
    WorkStatus status,
    String sort
) {
}
