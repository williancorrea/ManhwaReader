package dev.williancorrea.manhwa.reader.features.chapter.dto;

import java.util.UUID;

public record NextUnreadOutput(
    UUID id,
    String numberWithVersion,
    boolean hasReadChapters
) {}
