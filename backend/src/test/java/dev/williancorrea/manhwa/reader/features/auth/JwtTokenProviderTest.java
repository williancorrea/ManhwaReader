package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("JwtTokenProvider")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UUID userId;
    private String userEmail;
    private String userName;
    private String secret;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEmail = "test@example.com";
        userName = "Test User";
        secret = "my-secret-key-that-is-long-enough-for-256-bit";

        var jwtProperties = new JwtProperties();
        jwtProperties.setSecret(secret);
        jwtProperties.setAccessTokenExpiration(Duration.ofHours(1));
        jwtProperties.setRefreshTokenExpiration(Duration.ofDays(7));

        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
        jwtTokenProvider.init();
    }


    @Nested
    @DisplayName("generateAccessToken()")
    class GenerateAccessTokenTests {

        @Test
        @DisplayName("should generate valid access token")
        void shouldGenerateValidAccessToken() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();

            var claims = getClaims(token);
            assertThat(claims.getSubject()).isEqualTo(userId.toString());
            assertThat(claims.get("email")).isEqualTo(userEmail);
            assertThat(claims.get("name")).isEqualTo(userName);
        }

        @Test
        @DisplayName("should set correct expiration time for access token")
        void shouldSetCorrectExpiration() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);
            var claims = getClaims(token);

            var issuedAt = claims.getIssuedAt();
            var expiration = claims.getExpiration();

            assertThat(expiration).isAfter(issuedAt);
            var diffSeconds = (expiration.getTime() - issuedAt.getTime()) / 1000;
            assertThat(diffSeconds).isCloseTo(3600L, org.assertj.core.data.Offset.offset(10L));
        }

        @Test
        @DisplayName("should include all claims in access token")
        void shouldIncludeAllClaims() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);
            var claims = getClaims(token);

            assertThat(claims).containsKeys("sub", "email", "name", "iat", "exp");
            assertThat(claims.get("type")).isNull();
        }
    }

    @Nested
    @DisplayName("generateRefreshToken()")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("should generate valid refresh token")
        void shouldGenerateValidRefreshToken() {
            var token = jwtTokenProvider.generateRefreshToken(userId);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();

            var claims = getClaims(token);
            assertThat(claims.getSubject()).isEqualTo(userId.toString());
        }

        @Test
        @DisplayName("should include type claim in refresh token")
        void shouldIncludeTypeClaimInRefreshToken() {
            var token = jwtTokenProvider.generateRefreshToken(userId);
            var claims = getClaims(token);

            assertThat(claims.get("type")).isEqualTo("refresh");
        }

        @Test
        @DisplayName("should set correct expiration time for refresh token")
        void shouldSetCorrectExpirationForRefreshToken() {
            var token = jwtTokenProvider.generateRefreshToken(userId);
            var claims = getClaims(token);

            var issuedAt = claims.getIssuedAt();
            var expiration = claims.getExpiration();

            assertThat(expiration).isAfter(issuedAt);
            var diffSeconds = (expiration.getTime() - issuedAt.getTime()) / 1000;
            assertThat(diffSeconds).isCloseTo(604800L, org.assertj.core.data.Offset.offset(10L));
        }
    }

    @Nested
    @DisplayName("validateToken()")
    class ValidateTokenTests {

        @Test
        @DisplayName("should return true for valid token")
        void shouldReturnTrueForValidToken() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);

            var isValid = jwtTokenProvider.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("should return false for null token")
        void shouldReturnFalseForNullToken() {
            var isValid = jwtTokenProvider.validateToken(null);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for malformed token")
        void shouldReturnFalseForMalformedToken() {
            var isValid = jwtTokenProvider.validateToken("malformed.token");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false for expired token")
        void shouldReturnFalseForExpiredToken() {
            var expiredToken = Jwts.builder()
                    .subject(userId.toString())
                    .claim("email", userEmail)
                    .claim("name", userName)
                    .issuedAt(new Date(System.currentTimeMillis() - 10000))
                    .expiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(getSecretKey())
                    .compact();

            var isValid = jwtTokenProvider.validateToken(expiredToken);

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("isRefreshToken()")
    class IsRefreshTokenTests {

        @Test
        @DisplayName("should return true for refresh token")
        void shouldReturnTrueForRefreshToken() {
            var token = jwtTokenProvider.generateRefreshToken(userId);

            var isRefreshToken = jwtTokenProvider.isRefreshToken(token);

            assertThat(isRefreshToken).isTrue();
        }

        @Test
        @DisplayName("should return false for access token")
        void shouldReturnFalseForAccessToken() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);

            var isRefreshToken = jwtTokenProvider.isRefreshToken(token);

            assertThat(isRefreshToken).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid token")
        void shouldReturnFalseForInvalidToken() {
            var isRefreshToken = jwtTokenProvider.isRefreshToken("invalid.token");

            assertThat(isRefreshToken).isFalse();
        }

        @Test
        @DisplayName("should return false for null token")
        void shouldReturnFalseForNullToken() {
            var isRefreshToken = jwtTokenProvider.isRefreshToken(null);

            assertThat(isRefreshToken).isFalse();
        }
    }

    @Nested
    @DisplayName("getUserIdFromToken()")
    class GetUserIdFromTokenTests {

        @Test
        @DisplayName("should extract user id from token")
        void shouldExtractUserIdFromToken() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);

            var extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("should throw exception for invalid token")
        void shouldThrowExceptionForInvalidToken() {
            assertThatThrownBy(() -> jwtTokenProvider.getUserIdFromToken("invalid.token"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("getEmailFromToken()")
    class GetEmailFromTokenTests {

        @Test
        @DisplayName("should extract email from token")
        void shouldExtractEmailFromToken() {
            var token = jwtTokenProvider.generateAccessToken(userId, userEmail, userName);

            var extractedEmail = jwtTokenProvider.getEmailFromToken(token);

            assertThat(extractedEmail).isEqualTo(userEmail);
        }

        @Test
        @DisplayName("should return null email for refresh token")
        void shouldReturnNullEmailForRefreshToken() {
            var token = jwtTokenProvider.generateRefreshToken(userId);

            var extractedEmail = jwtTokenProvider.getEmailFromToken(token);

            assertThat(extractedEmail).isNull();
        }

        @Test
        @DisplayName("should throw exception for invalid token")
        void shouldThrowExceptionForInvalidToken() {
            assertThatThrownBy(() -> jwtTokenProvider.getEmailFromToken("invalid.token"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("getAccessTokenExpirationSeconds()")
    class GetAccessTokenExpirationSecondsTests {

        @Test
        @DisplayName("should return correct expiration seconds")
        void shouldReturnCorrectExpirationSeconds() {
            var seconds = jwtTokenProvider.getAccessTokenExpirationSeconds();

            assertThat(seconds).isEqualTo(3600L);
        }
    }

    @Nested
    @DisplayName("getRefreshTokenExpirationSeconds()")
    class GetRefreshTokenExpirationSecondsTests {

        @Test
        @DisplayName("should return correct refresh token expiration seconds")
        void shouldReturnCorrectRefreshTokenExpirationSeconds() {
            var seconds = jwtTokenProvider.getRefreshTokenExpirationSeconds();

            assertThat(seconds).isEqualTo(604800L);
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
