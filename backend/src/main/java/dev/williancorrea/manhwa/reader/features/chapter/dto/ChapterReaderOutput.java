package dev.williancorrea.manhwa.reader.features.chapter.dto;

import java.util.List;
import java.util.UUID;

public record ChapterReaderOutput(
    UUID id,
    String number,
    String numberWithVersion,
    String title,
    String workTitle,
    String workSlug,
    List<ChapterPageOutput> pages,
    ChapterNavOutput previousChapter,
    ChapterNavOutput nextChapter
) {}
