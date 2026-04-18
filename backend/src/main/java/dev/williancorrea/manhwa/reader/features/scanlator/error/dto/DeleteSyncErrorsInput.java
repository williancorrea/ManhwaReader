package dev.williancorrea.manhwa.reader.features.scanlator.error.dto;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record DeleteSyncErrorsInput(
    @NotEmpty
    @Size(max = 200)
    List<UUID> ids
) {
}
