package dev.williancorrea.manhwa.reader.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginInput(
        @NotBlank String idToken
) {
}
