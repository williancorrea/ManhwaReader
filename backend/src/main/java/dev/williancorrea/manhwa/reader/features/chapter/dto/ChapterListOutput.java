package dev.williancorrea.manhwa.reader.features.chapter.dto;

import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgress;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ChapterListOutput(
    UUID id,
    String number,
    String numberFormatted,
    String numberWithVersion,
    String title,
    String language,
    LocalDate releaseDate,
    String scanlator,
    Integer volume,
    boolean isRead,
    int readProgress,
    OffsetDateTime publishedAt
) {

  public static ChapterListOutput fromEntity(Chapter chapter, ReadingProgress progress) {
    boolean isRead = progress != null;
    int readProgress = progress != null && progress.getPageNumber() != null ? progress.getPageNumber() : 0;

    return new ChapterListOutput(
        chapter.getId(),
        chapter.getNumber(),
        chapter.getNumberFormatted(),
        chapter.getNumberWithVersionInteger(),
        chapter.getTitle(),
        chapter.getLanguage() != null ? chapter.getLanguage().getCode() : null,
        chapter.getReleaseDate(),
        chapter.getScanlator() != null ? chapter.getScanlator().getName() : null,
        chapter.getVolume() != null ? chapter.getVolume().getNumber() : null,
        isRead,
        readProgress,
        chapter.getPublishedAt()
    );
  }
}
