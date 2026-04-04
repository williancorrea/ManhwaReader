package dev.williancorrea.manhwa.reader.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record AuthOutput(
        String accessToken,
        long expiresIn,
        @JsonInclude(JsonInclude.Include.NON_NULL) Boolean isNewUser
) {
}
