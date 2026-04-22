package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.features.auth.dto.AuthOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthResult")
class AuthResultTest {

    private AuthOutput authOutput;
    private ResponseCookie cookie;

    @BeforeEach
    void setUp() {
        authOutput = new AuthOutput("accessToken123", 3600L, null);
        cookie = ResponseCookie.from("refreshToken", "refreshTokenValue")
                .httpOnly(true)
                .build();
    }

    @Test
    @DisplayName("should create AuthResult with output and cookie")
    void shouldCreateAuthResult() {
        var result = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result).isNotNull();
        assertThat(result.output()).isEqualTo(authOutput);
        assertThat(result.refreshTokenCookie()).isEqualTo(cookie);
    }

    @Test
    @DisplayName("should access output method")
    void shouldAccessOutputMethod() {
        var result = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result.output().accessToken()).isEqualTo("accessToken123");
        assertThat(result.output().expiresIn()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("should access refreshTokenCookie method")
    void shouldAccessRefreshTokenCookieMethod() {
        var result = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result.refreshTokenCookie()).isNotNull();
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("refreshTokenValue");
    }

    @Test
    @DisplayName("should be equal for same values")
    void shouldBeEqualForSameValues() {
        var result1 = new AuthService.AuthResult(authOutput, cookie);
        var result2 = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("should have consistent hash code")
    void shouldHaveConsistentHashCode() {
        var result1 = new AuthService.AuthResult(authOutput, cookie);
        var result2 = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("should handle null cookie")
    void shouldHandleNullCookie() {
        var result = new AuthService.AuthResult(authOutput, null);

        assertThat(result).isNotNull();
        assertThat(result.output()).isEqualTo(authOutput);
        assertThat(result.refreshTokenCookie()).isNull();
    }

    @Test
    @DisplayName("should have meaningful toString representation")
    void shouldHaveMeaningfulToString() {
        var result = new AuthService.AuthResult(authOutput, cookie);

        assertThat(result.toString()).isNotNull();
    }
}
