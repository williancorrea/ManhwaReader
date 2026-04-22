package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.features.access.user.UserOutput;
import dev.williancorrea.manhwa.reader.features.auth.dto.AuthOutput;
import dev.williancorrea.manhwa.reader.features.auth.dto.GoogleLoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.LoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.RegisterInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthResource")
class AuthResourceTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthResource authResource;

    private UUID userId;
    private String userEmail;
    private String userName;
    private ResponseCookie mockCookie;
    private AuthOutput authOutput;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEmail = "test@example.com";
        userName = "Test User";
        authOutput = new AuthOutput("accessToken", 3600L, null);
        mockCookie = ResponseCookie.from("refreshToken", "refreshTokenValue")
                .httpOnly(true)
                .build();
    }

    @Nested
    @DisplayName("me()")
    class MeTests {

        @Test
        @DisplayName("should return current user info")
        void shouldReturnCurrentUserInfo() {
            var userOutput = mock(UserOutput.class);
            var userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn(userEmail);
            when(authService.getMe(userEmail)).thenReturn(userOutput);

            var response = authResource.me(userDetails);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(userOutput);
            verify(authService).getMe(userEmail);
        }
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("should register new user with 201 status")
        void shouldRegisterNewUserWithCreatedStatus() {
            var input = new RegisterInput(userName, userEmail, "password123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.register(input)).thenReturn(authResult);

            var response = authResource.register(input);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(authOutput);
            assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).isNotNull();
            verify(authService).register(input);
        }

        @Test
        @DisplayName("should include refresh token cookie in response")
        void shouldIncludeRefreshTokenCookie() {
            var input = new RegisterInput(userName, userEmail, "password123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.register(input)).thenReturn(authResult);

            var response = authResource.register(input);

            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("should login user with 200 status")
        void shouldLoginUserWithOkStatus() {
            var input = new LoginInput(userEmail, "password123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.login(input)).thenReturn(authResult);

            var response = authResource.login(input);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(authOutput);
            verify(authService).login(input);
        }

        @Test
        @DisplayName("should include refresh token cookie in response")
        void shouldIncludeRefreshTokenCookie() {
            var input = new LoginInput(userEmail, "password123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.login(input)).thenReturn(authResult);

            var response = authResource.login(input);

            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("googleLogin()")
    class GoogleLoginTests {

        @Test
        @DisplayName("should login with Google token")
        void shouldGoogleLogin() {
            var input = new GoogleLoginInput("idToken123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.googleLogin(input)).thenReturn(authResult);

            var response = authResource.googleLogin(input);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(authOutput);
            verify(authService).googleLogin(input);
        }

        @Test
        @DisplayName("should include refresh token cookie in response")
        void shouldIncludeRefreshTokenCookie() {
            var input = new GoogleLoginInput("idToken123");
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.googleLogin(input)).thenReturn(authResult);

            var response = authResource.googleLogin(input);

            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("refresh()")
    class RefreshTests {

        @Test
        @DisplayName("should refresh token")
        void shouldRefreshToken() {
            var refreshToken = "refreshToken123";
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.refresh(refreshToken)).thenReturn(authResult);

            var response = authResource.refresh(refreshToken);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(authOutput);
            verify(authService).refresh(refreshToken);
        }

        @Test
        @DisplayName("should handle null refresh token")
        void shouldHandleNullRefreshToken() {
            var authResult = new AuthService.AuthResult(authOutput, mockCookie);

            when(authService.refresh(null)).thenReturn(authResult);

            var response = authResource.refresh(null);

            assertThat(response).isNotNull();
            verify(authService).refresh(null);
        }
    }

    @Nested
    @DisplayName("logout()")
    class LogoutTests {

        @Test
        @DisplayName("should logout with 204 status")
        void shouldLogoutWithNoContentStatus() {
            var logoutCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .build();

            when(authService.buildLogoutCookie()).thenReturn(logoutCookie);

            var response = authResource.logout();

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(authService).buildLogoutCookie();
        }

        @Test
        @DisplayName("should include logout cookie in response")
        void shouldIncludeLogoutCookie() {
            var logoutCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .build();

            when(authService.buildLogoutCookie()).thenReturn(logoutCookie);

            var response = authResource.logout();

            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotEmpty();
        }
    }
}
