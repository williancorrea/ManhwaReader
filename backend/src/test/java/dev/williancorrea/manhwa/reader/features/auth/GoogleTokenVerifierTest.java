package dev.williancorrea.manhwa.reader.features.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import dev.williancorrea.manhwa.reader.config.GoogleProperties;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleTokenVerifier")
class GoogleTokenVerifierTest {

    @Mock
    private GoogleProperties googleProperties;

    @Mock
    private GoogleIdTokenVerifier idTokenVerifier;

    private GoogleTokenVerifier googleTokenVerifier;
    private String validToken;
    private String googleId;
    private String email;
    private String name;
    private String pictureUrl;

    @BeforeEach
    void setUp() throws Exception {
        googleId = "123456789";
        email = "test@gmail.com";
        name = "Test User";
        pictureUrl = "https://example.com/picture.jpg";
        validToken = "valid.google.token";

        when(googleProperties.getClientId()).thenReturn("client-id");

        googleTokenVerifier = new GoogleTokenVerifier(googleProperties);

        Field verifierField = GoogleTokenVerifier.class.getDeclaredField("verifier");
        verifierField.setAccessible(true);
        verifierField.set(googleTokenVerifier, idTokenVerifier);
    }

    @Nested
    @DisplayName("verify()")
    class VerifyTests {

        @Test
        @DisplayName("should return GoogleUserInfo when token is valid")
        void shouldReturnGoogleUserInfoWhenTokenIsValid() throws Exception {
            var payload = mock(GoogleIdToken.Payload.class);
            when(payload.getSubject()).thenReturn(googleId);
            when(payload.getEmail()).thenReturn(email);
            when(payload.getEmailVerified()).thenReturn(Boolean.TRUE);
            when(payload.get("name")).thenReturn(name);
            when(payload.get("picture")).thenReturn(pictureUrl);

            var idToken = mock(GoogleIdToken.class);
            when(idToken.getPayload()).thenReturn(payload);
            when(idTokenVerifier.verify(validToken)).thenReturn(idToken);

            var result = googleTokenVerifier.verify(validToken);

            assertThat(result.getGoogleId()).isEqualTo(googleId);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.isEmailVerified()).isTrue();
            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getPictureUrl()).isEqualTo(pictureUrl);
        }

        @Test
        @DisplayName("should return GoogleUserInfo with null name and picture when payload has none")
        void shouldReturnGoogleUserInfoWithNullOptionalFields() throws Exception {
            var payload = mock(GoogleIdToken.Payload.class);
            when(payload.getSubject()).thenReturn(googleId);
            when(payload.getEmail()).thenReturn(email);
            when(payload.getEmailVerified()).thenReturn(Boolean.FALSE);
            when(payload.get("name")).thenReturn(null);
            when(payload.get("picture")).thenReturn(null);

            var idToken = mock(GoogleIdToken.class);
            when(idToken.getPayload()).thenReturn(payload);
            when(idTokenVerifier.verify(validToken)).thenReturn(idToken);

            var result = googleTokenVerifier.verify(validToken);

            assertThat(result.getGoogleId()).isEqualTo(googleId);
            assertThat(result.isEmailVerified()).isFalse();
            assertThat(result.getName()).isNull();
            assertThat(result.getPictureUrl()).isNull();
        }

        @Test
        @DisplayName("should throw BusinessException when verifier returns null")
        void shouldThrowBusinessExceptionWhenVerifierReturnsNull() throws Exception {
            when(idTokenVerifier.verify(validToken)).thenReturn(null);

            assertThatThrownBy(() -> googleTokenVerifier.verify(validToken))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("should throw BusinessException when GeneralSecurityException is thrown")
        void shouldThrowBusinessExceptionOnGeneralSecurityException() throws Exception {
            when(idTokenVerifier.verify(validToken)).thenThrow(new GeneralSecurityException("security error"));

            assertThatThrownBy(() -> googleTokenVerifier.verify(validToken))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("should throw BusinessException when IOException is thrown")
        void shouldThrowBusinessExceptionOnIOException() throws Exception {
            when(idTokenVerifier.verify(validToken)).thenThrow(new IOException("io error"));

            assertThatThrownBy(() -> googleTokenVerifier.verify(validToken))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("GoogleUserInfo")
    class GoogleUserInfoTests {

        @Test
        @DisplayName("should create GoogleUserInfo with all fields")
        void shouldCreateGoogleUserInfoWithAllFields() {
            var userInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    googleId, email, true, name, pictureUrl
            );

            assertThat(userInfo.getGoogleId()).isEqualTo(googleId);
            assertThat(userInfo.getEmail()).isEqualTo(email);
            assertThat(userInfo.isEmailVerified()).isTrue();
            assertThat(userInfo.getName()).isEqualTo(name);
            assertThat(userInfo.getPictureUrl()).isEqualTo(pictureUrl);
        }

        @Test
        @DisplayName("should create GoogleUserInfo with null name")
        void shouldCreateGoogleUserInfoWithNullName() {
            var userInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    googleId, email, true, null, pictureUrl
            );

            assertThat(userInfo.getName()).isNull();
        }

        @Test
        @DisplayName("should create GoogleUserInfo with null picture url")
        void shouldCreateGoogleUserInfoWithNullPictureUrl() {
            var userInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    googleId, email, true, name, null
            );

            assertThat(userInfo.getPictureUrl()).isNull();
        }

        @Test
        @DisplayName("should create GoogleUserInfo with email not verified")
        void shouldCreateGoogleUserInfoWithEmailNotVerified() {
            var userInfo = new GoogleTokenVerifier.GoogleUserInfo(
                    googleId, email, false, name, pictureUrl
            );

            assertThat(userInfo.isEmailVerified()).isFalse();
        }
    }
}
