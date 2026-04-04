package dev.williancorrea.manhwa.reader.features.auth;

import dev.williancorrea.manhwa.reader.features.auth.dto.AuthOutput;
import dev.williancorrea.manhwa.reader.features.auth.dto.GoogleLoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.LoginInput;
import dev.williancorrea.manhwa.reader.features.auth.dto.RegisterInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthOutput> register(@RequestBody @Valid RegisterInput input) {
        var result = authService.register(input);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.output());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthOutput> login(@RequestBody @Valid LoginInput input) {
        var result = authService.login(input);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.output());
    }

    @PostMapping("/google")
    public ResponseEntity<AuthOutput> googleLogin(@RequestBody @Valid GoogleLoginInput input) {
        var result = authService.googleLogin(input);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString())
                .body(result.output());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthOutput> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        var result = authService.refresh(refreshToken);
        return ResponseEntity.ok(result.output());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        var cookie = authService.buildLogoutCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
