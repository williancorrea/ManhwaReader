package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.config.CookieProperties;
import dev.williancorrea.manhwa.reader.config.JwtProperties;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserOutput;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.auth.dto.AuthOutput;
import dev.williancorrea.manhwa.reader.features.auth.dto.GoogleLoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.LoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.RegisterInput;
import dev.williancorrea.manhwa.reader.security.DatabaseUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final @Lazy UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtProperties jwtProperties;
    private final CookieProperties cookieProperties;
    private final DatabaseUserDetailsService databaseUserDetailsService;

    @Transactional
    public AuthResult register(RegisterInput input) {
        if (userRepository.existsByEmail(input.email())) {
            throw new ConflictException("auth.error.email-already-exists", null);
        }

        var user = User.builder()
                .name(input.name())
                .email(input.email())
                .passwordHash(passwordEncoder.encode(input.password()))
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("Novo usu\u00e1rio registrado: {}", user.getEmail());

        return buildAuthResult(user, null);
    }

    public AuthResult login(LoginInput input) {
        var user = userRepository.findByEmail(input.email())
                .orElseThrow(() -> new BadCredentialsException("auth.error.invalid-credentials"));

        if (!user.hasPasswordLogin()) {
            throw new BusinessException("auth.error.google-account", null);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.email(), input.password()));

        log.info("Login bem-sucedido: {}", user.getEmail());
        return buildAuthResult(user, null);
    }

    @Transactional
    public AuthResult googleLogin(GoogleLoginInput input) {
        var googleInfo = googleTokenVerifier.verify(input.idToken());

        if (!googleInfo.isEmailVerified()) {
            throw new BusinessException("auth.error.google-email-not-verified", null);
        }

        // Try to find by googleId first
        var existingByGoogleId = userRepository.findByGoogleId(googleInfo.getGoogleId());
        if (existingByGoogleId.isPresent()) {
            var user = existingByGoogleId.get();
            log.info("Login Google existente: {}", user.getEmail());
            return buildAuthResult(user, false);
        }

        // Try to find by email for account linking
        var existingByEmail = userRepository.findByEmail(googleInfo.getEmail());
        if (existingByEmail.isPresent()) {
            var user = existingByEmail.get();
            user.setGoogleId(googleInfo.getGoogleId());
            if (googleInfo.getPictureUrl() != null) {
                user.setAvatarUrl(googleInfo.getPictureUrl());
            }
            user.setEmailVerified(true);
            userRepository.save(user);
            log.info("Conta vinculada ao Google: {}", user.getEmail());
            return buildAuthResult(user, false);
        }

        // Create new user
        var newUser = User.builder()
                .name(googleInfo.getName() != null ? googleInfo.getName() : googleInfo.getEmail())
                .email(googleInfo.getEmail())
                .googleId(googleInfo.getGoogleId())
                .avatarUrl(googleInfo.getPictureUrl())
                .emailVerified(true)
                .build();

        newUser = userRepository.save(newUser);
        log.info("Novo usu\u00e1rio Google criado: {}", newUser.getEmail());
        return buildAuthResult(newUser, true);
    }

    public AuthResult refresh(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)
                || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException("auth.error.invalid-refresh-token", null);
        }

        var userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("auth.error.invalid-refresh-token", null));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getName());
        return new AuthResult(
                new AuthOutput(accessToken, jwtTokenProvider.getAccessTokenExpirationSeconds(), null),
                null
        );
    }

    public UserOutput getMe(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("auth.error.user-not-found", null));
        var userDetails = databaseUserDetailsService.loadUserByUsername(email);
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new UserOutput(user, roles);
    }

    public ResponseCookie buildLogoutCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
    }

    private AuthResult buildAuthResult(User user, Boolean isNewUser) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        var output = new AuthOutput(accessToken, jwtTokenProvider.getAccessTokenExpirationSeconds(), isNewUser);
        var cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path("/api/v1/auth")
                .maxAge(jwtTokenProvider.getRefreshTokenExpirationSeconds())
                .build();

        return new AuthResult(output, cookie);
    }

    public record AuthResult(AuthOutput output, ResponseCookie refreshTokenCookie) {
    }
}
