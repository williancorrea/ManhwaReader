package dev.williancorrea.manhwa.reader.features.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dev.williancorrea.manhwa.reader.config.GoogleProperties;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(GoogleProperties googleProperties) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleProperties.getClientId()))
                .build();
    }

    public GoogleUserInfo verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException("auth.error.invalid-google-token", null);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            return new GoogleUserInfo(
                    payload.getSubject(),
                    payload.getEmail(),
                    Boolean.TRUE.equals(payload.getEmailVerified()),
                    (String) payload.get("name"),
                    (String) payload.get("picture")
            );
        } catch (GeneralSecurityException | IOException e) {
            log.error("Erro ao validar token Google: {}", e.getMessage());
            throw new BusinessException("auth.error.invalid-google-token", null);
        }
    }

    @Getter
    public static class GoogleUserInfo {
        private final String googleId;
        private final String email;
        private final boolean emailVerified;
        private final String name;
        private final String pictureUrl;

        public GoogleUserInfo(String googleId, String email, boolean emailVerified, String name, String pictureUrl) {
            this.googleId = googleId;
            this.email = email;
            this.emailVerified = emailVerified;
            this.name = name;
            this.pictureUrl = pictureUrl;
        }
    }
}
