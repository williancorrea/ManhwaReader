package dev.williancorrea.manhwa.reader.features.chapter.dto;

public record ChapterPageOutput(
    int pageNumber,
    String type,
    String imageUrl,
    String content
) {}
