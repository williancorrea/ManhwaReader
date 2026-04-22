package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.config.CookieProperties;
import dev.williancorrea.manhwa.reader.config.JwtProperties;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.auth.dto.GoogleLoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.LoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.RegisterInput;
import dev.williancorrea.manhwa.reader.security.DatabaseUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private CookieProperties cookieProperties;

    @Mock
    private DatabaseUserDetailsService databaseUserDetailsService;

    @InjectMocks
    private AuthService authService;

    private UUID userId;
    private String userEmail;
    private String userName;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEmail = "test@example.com";
        userName = "Test User";
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("should register new user successfully")
        void shouldRegisterNewUser() {
            var input = new RegisterInput(userName, userEmail, "password123");
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .passwordHash("encodedPassword")
                    .emailVerified(false)
                    .build();

            when(userRepository.existsByEmail(userEmail)).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.register(input);

            assertThat(result).isNotNull();
            assertThat(result.output().accessToken()).isEqualTo("accessToken");
            assertThat(result.refreshTokenCookie()).isNotNull();
            verify(userRepository).existsByEmail(userEmail);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ConflictException when email already exists")
        void shouldThrowConflictExceptionWhenEmailExists() {
            var input = new RegisterInput(userName, userEmail, "password123");

            when(userRepository.existsByEmail(userEmail)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(input))
                    .isInstanceOf(ConflictException.class);

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("should login user successfully with correct credentials")
        void shouldLoginSuccessfully() {
            var input = new LoginInput(userEmail, "password123");
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .passwordHash("encodedPassword")
                    .build();

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(userEmail, null));
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.login(input);

            assertThat(result).isNotNull();
            assertThat(result.output().accessToken()).isEqualTo("accessToken");
            verify(userRepository).findByEmail(userEmail);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("should throw BadCredentialsException when user not found")
        void shouldThrowBadCredentialsWhenUserNotFound() {
            var input = new LoginInput(userEmail, "password123");

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(input))
                    .isInstanceOf(BadCredentialsException.class);

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("should throw BusinessException when user has no password login")
        void shouldThrowBusinessExceptionWhenNoPasswordLogin() {
            var input = new LoginInput(userEmail, "password123");
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();
            user.setGoogleId("googleId123");

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.login(input))
                    .isInstanceOf(BusinessException.class);

            verify(authenticationManager, never()).authenticate(any());
        }
    }

    @Nested
    @DisplayName("googleLogin()")
    class GoogleLoginTests {

        @Test
        @DisplayName("should login existing user by googleId")
        void shouldLoginExistingUserByGoogleId() {
            var input = new GoogleLoginInput("idToken123");
            var googleInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    "googleId123", userEmail, true, userName, "pictureUrl"
            );
            var existingUser = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .googleId("googleId123")
                    .build();

            when(googleTokenVerifier.verify("idToken123")).thenReturn(googleInfo);
            when(userRepository.findByGoogleId("googleId123")).thenReturn(Optional.of(existingUser));
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.googleLogin(input);

            assertThat(result).isNotNull();
            assertThat(result.output().accessToken()).isEqualTo("accessToken");
            verify(userRepository).findByGoogleId("googleId123");
        }

        @Test
        @DisplayName("should link Google account to existing email account")
        void shouldLinkGoogleAccountToExistingEmail() {
            var input = new GoogleLoginInput("idToken123");
            var googleInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    "googleId123", userEmail, true, userName, "pictureUrl"
            );
            var existingUser = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();

            when(googleTokenVerifier.verify("idToken123")).thenReturn(googleInfo);
            when(userRepository.findByGoogleId("googleId123")).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.googleLogin(input);

            assertThat(result).isNotNull();
            assertThat(result.output().accessToken()).isEqualTo("accessToken");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should create new user from Google info")
        void shouldCreateNewUserFromGoogleInfo() {
            var input = new GoogleLoginInput("idToken123");
            var googleInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    "googleId123", userEmail, true, userName, "pictureUrl"
            );
            var newUser = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .googleId("googleId123")
                    .avatarUrl("pictureUrl")
                    .emailVerified(true)
                    .build();

            when(googleTokenVerifier.verify("idToken123")).thenReturn(googleInfo);
            when(userRepository.findByGoogleId("googleId123")).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(newUser);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.googleLogin(input);

            assertThat(result).isNotNull();
            assertThat(result.output().isNewUser()).isTrue();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should create new user with email when Google info has no name")
        void shouldCreateNewUserWithEmailAsName() {
            var input = new GoogleLoginInput("idToken123");
            var googleInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    "googleId123", userEmail, true, null, null
            );
            var newUser = User.builder()
                    .id(userId)
                    .name(userEmail)
                    .email(userEmail)
                    .googleId("googleId123")
                    .emailVerified(true)
                    .build();

            when(googleTokenVerifier.verify("idToken123")).thenReturn(googleInfo);
            when(userRepository.findByGoogleId("googleId123")).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(newUser);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userEmail)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.googleLogin(input);

            assertThat(result).isNotNull();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw BusinessException when email not verified")
        void shouldThrowBusinessExceptionWhenEmailNotVerified() {
            var input = new GoogleLoginInput("idToken123");
            var googleInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    "googleId123", userEmail, false, userName, "pictureUrl"
            );

            when(googleTokenVerifier.verify("idToken123")).thenReturn(googleInfo);

            assertThatThrownBy(() -> authService.googleLogin(input))
                    .isInstanceOf(BusinessException.class);

            verify(userRepository, never()).findByGoogleId(anyString());
        }
    }

    @Nested
    @DisplayName("refresh()")
    class RefreshTests {

        @Test
        @DisplayName("should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            var refreshToken = "refreshToken123";
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();

            when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("newAccessToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);

            var result = authService.refresh(refreshToken);

            assertThat(result).isNotNull();
            assertThat(result.output().accessToken()).isEqualTo("newAccessToken");
            verify(jwtTokenProvider).validateToken(refreshToken);
            verify(jwtTokenProvider).isRefreshToken(refreshToken);
        }

        @Test
        @DisplayName("should throw BusinessException when refresh token is null")
        void shouldThrowBusinessExceptionWhenTokenNull() {
            assertThatThrownBy(() -> authService.refresh(null))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("should throw BusinessException when refresh token is invalid")
        void shouldThrowBusinessExceptionWhenTokenInvalid() {
            var refreshToken = "invalidToken";

            when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(false);

            assertThatThrownBy(() -> authService.refresh(refreshToken))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("should throw BusinessException when token is not refresh token")
        void shouldThrowBusinessExceptionWhenNotRefreshToken() {
            var refreshToken = "accessToken123";

            when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(false);

            assertThatThrownBy(() -> authService.refresh(refreshToken))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("should throw BusinessException when user not found")
        void shouldThrowBusinessExceptionWhenUserNotFound() {
            var refreshToken = "refreshToken123";

            when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refresh(refreshToken))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("getMe()")
    class GetMeTests {

        @Test
        @DisplayName("should return user info successfully")
        void shouldReturnUserInfo() {
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();
            var mockAuthority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER");
            var userDetails = mock(org.springframework.security.core.userdetails.UserDetails.class);
            when(userDetails.getAuthorities()).thenAnswer(invocation -> List.of(mockAuthority));

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
            when(databaseUserDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);

            var result = authService.getMe(userEmail);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(userEmail);
            assertThat(result.getName()).isEqualTo(userName);
            verify(userRepository).findByEmail(userEmail);
            verify(databaseUserDetailsService).loadUserByUsername(userEmail);
        }

        @Test
        @DisplayName("should throw BusinessException when user not found")
        void shouldThrowBusinessExceptionWhenUserNotFound() {
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.getMe(userEmail))
                    .isInstanceOf(BusinessException.class);

            verify(databaseUserDetailsService, never()).loadUserByUsername(anyString());
        }
    }

    @Nested
    @DisplayName("buildLogoutCookie()")
    class BuildLogoutCookieTests {

        @Test
        @DisplayName("should build logout cookie with empty refresh token")
        void shouldBuildLogoutCookie() {
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var cookie = authService.buildLogoutCookie();

            assertThat(cookie).isNotNull();
            assertThat(cookie.getValue()).isEmpty();
            assertThat(cookie.getMaxAge()).isZero();
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.isSecure()).isTrue();
        }

        @Test
        @DisplayName("should build logout cookie with correct attributes")
        void shouldBuildLogoutCookieWithCorrectAttributes() {
            when(cookieProperties.isSecure()).thenReturn(false);
            when(cookieProperties.getSameSite()).thenReturn("Lax");

            var cookie = authService.buildLogoutCookie();

            assertThat(cookie).isNotNull();
            assertThat(cookie.getPath()).isEqualTo("/api/v1/auth");
            assertThat(cookie.isHttpOnly()).isTrue();
        }
    }

    @Nested
    @DisplayName("buildAuthResult()")
    class BuildAuthResultTests {

        @Test
        @DisplayName("should build auth result with tokens and cookie")
        void shouldBuildAuthResult() {
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();

            when(userRepository.existsByEmail(userEmail)).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(jwtTokenProvider.getRefreshTokenExpirationSeconds()).thenReturn(86400L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.register(new RegisterInput(userName, userEmail, "password123"));

            assertThat(result).isNotNull();
            assertThat(result.refreshTokenCookie()).isNotNull();
        }

        @Test
        @DisplayName("should mark new user as true when isNewUser is true")
        void shouldMarkNewUserAsTrue() {
            var user = User.builder()
                    .id(userId)
                    .name(userName)
                    .email(userEmail)
                    .build();

            when(userRepository.existsByEmail(userEmail)).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtTokenProvider.generateAccessToken(userId, userEmail, userName)).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("refreshToken");
            when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
            when(cookieProperties.isSecure()).thenReturn(true);
            when(cookieProperties.getSameSite()).thenReturn("Strict");

            var result = authService.register(new RegisterInput(userName, userEmail, "password123"));

            assertThat(result.output().isNewUser()).isNull();
        }
    }
}
