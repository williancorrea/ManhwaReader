package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.security.DatabaseUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private DatabaseUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private String userEmail;
    private String token;

    @BeforeEach
    void setUp() {
        userEmail = "test@example.com";
        token = "validToken123";
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal()")
    class DoFilterInternalTests {

        @Test
        @DisplayName("should authenticate user and set SecurityContext with valid access token")
        void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
            var userDetails = mock(UserDetails.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(token)).thenReturn(false);
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(userEmail);
            when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.isAuthenticated()).isTrue();
            assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should not authenticate without Authorization header")
        void shouldNotAuthenticateWithoutAuthorizationHeader() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
            verify(jwtTokenProvider, never()).validateToken(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should not authenticate with invalid Bearer format")
        void shouldNotAuthenticateWithInvalidBearerFormat() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("InvalidBearer " + token);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
            verify(jwtTokenProvider, never()).validateToken(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should not authenticate with invalid token")
        void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
            verify(jwtTokenProvider, never()).isRefreshToken(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should not authenticate if token is refresh token")
        void shouldNotAuthenticateIfRefreshToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(token)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
            verify(jwtTokenProvider, never()).getEmailFromToken(token);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should continue filter chain even on authentication error")
        void shouldContinueFilterChainOnAuthenticationError() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.isRefreshToken(token)).thenReturn(false);
            when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(userEmail);
            when(userDetailsService.loadUserByUsername(userEmail))
                    .thenThrow(new RuntimeException("User not found"));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should handle empty Bearer token")
        void shouldHandleEmptyBearerToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }
}
