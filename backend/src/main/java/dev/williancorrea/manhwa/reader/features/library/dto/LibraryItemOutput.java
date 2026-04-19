package dev.williancorrea.manhwa.reader.features.library.dto;

import java.util.UUID;

public record LibraryItemOutput(
    UUID workId,
    String slug,
    String title,
    String coverUrl,
    String publicationDemographic,
    String workStatus,
    Long chapterCount,
    String libraryStatus,
    Long unreadCount,
    String originalLanguageCode,
    String originalLanguageFlag,
    String originalLanguageName
) {
}
