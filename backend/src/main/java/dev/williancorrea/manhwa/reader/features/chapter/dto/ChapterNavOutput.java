package dev.williancorrea.manhwa.reader.features.chapter.dto;

import java.util.UUID;

public record ChapterNavOutput(
    UUID id,
    String numberWithVersion
) {}
