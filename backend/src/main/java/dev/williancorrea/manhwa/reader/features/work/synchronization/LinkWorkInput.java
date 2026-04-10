package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LinkWorkInput(
    @NotNull UUID workId,
    @NotBlank String mangaDexId
) {
}
