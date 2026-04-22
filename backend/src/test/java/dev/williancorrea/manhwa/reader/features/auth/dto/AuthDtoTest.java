package dev.williancorrea.manhwa.reader.features.auth.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Auth DTOs")
class AuthDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("LoginInput")
    class LoginInputTests {

        @Test
        @DisplayName("should create LoginInput with email and password")
        void shouldCreateLoginInput() {
            var input = new LoginInput("test@example.com", "password123");

            assertThat(input.email()).isEqualTo("test@example.com");
            assertThat(input.password()).isEqualTo("password123");
        }

        @Test
        @DisplayName("should be equal for same values")
        void shouldBeEqualForSameValues() {
            var input1 = new LoginInput("test@example.com", "password123");
            var input2 = new LoginInput("test@example.com", "password123");

            assertThat(input1).isEqualTo(input2);
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            var input1 = new LoginInput("test@example.com", "password123");
            var input2 = new LoginInput("other@example.com", "password123");

            assertThat(input1).isNotEqualTo(input2);
        }
    }

    @Nested
    @DisplayName("RegisterInput")
    class RegisterInputTests {

        @Test
        @DisplayName("should create RegisterInput with name, email and password")
        void shouldCreateRegisterInput() {
            var input = new RegisterInput("Test User", "test@example.com", "password123");

            assertThat(input.name()).isEqualTo("Test User");
            assertThat(input.email()).isEqualTo("test@example.com");
            assertThat(input.password()).isEqualTo("password123");
        }

        @Test
        @DisplayName("should be equal for same values")
        void shouldBeEqualForSameValues() {
            var input1 = new RegisterInput("Test User", "test@example.com", "password123");
            var input2 = new RegisterInput("Test User", "test@example.com", "password123");

            assertThat(input1).isEqualTo(input2);
        }

        @Test
        @DisplayName("should not be equal for different names")
        void shouldNotBeEqualForDifferentNames() {
            var input1 = new RegisterInput("Test User", "test@example.com", "password123");
            var input2 = new RegisterInput("Other User", "test@example.com", "password123");

            assertThat(input1).isNotEqualTo(input2);
        }
    }

    @Nested
    @DisplayName("GoogleLoginInput")
    class GoogleLoginInputTests {

        @Test
        @DisplayName("should create GoogleLoginInput with idToken")
        void shouldCreateGoogleLoginInput() {
            var input = new GoogleLoginInput("idToken123");

            assertThat(input.idToken()).isEqualTo("idToken123");
        }

        @Test
        @DisplayName("should be equal for same values")
        void shouldBeEqualForSameValues() {
            var input1 = new GoogleLoginInput("idToken123");
            var input2 = new GoogleLoginInput("idToken123");

            assertThat(input1).isEqualTo(input2);
        }

        @Test
        @DisplayName("should not be equal for different idTokens")
        void shouldNotBeEqualForDifferentIdTokens() {
            var input1 = new GoogleLoginInput("idToken123");
            var input2 = new GoogleLoginInput("idToken456");

            assertThat(input1).isNotEqualTo(input2);
        }
    }

    @Nested
    @DisplayName("AuthOutput")
    class AuthOutputTests {

        @Test
        @DisplayName("should create AuthOutput with accessToken and expiresIn")
        void shouldCreateAuthOutput() {
            var output = new AuthOutput("accessToken123", 3600L, null);

            assertThat(output.accessToken()).isEqualTo("accessToken123");
            assertThat(output.expiresIn()).isEqualTo(3600L);
            assertThat(output.isNewUser()).isNull();
        }

        @Test
        @DisplayName("should create AuthOutput with isNewUser flag")
        void shouldCreateAuthOutputWithIsNewUser() {
            var output = new AuthOutput("accessToken123", 3600L, true);

            assertThat(output.isNewUser()).isTrue();
        }

        @Test
        @DisplayName("should be equal for same values")
        void shouldBeEqualForSameValues() {
            var output1 = new AuthOutput("accessToken123", 3600L, null);
            var output2 = new AuthOutput("accessToken123", 3600L, null);

            assertThat(output1).isEqualTo(output2);
        }

        @Test
        @DisplayName("should not be equal for different accessTokens")
        void shouldNotBeEqualForDifferentAccessTokens() {
            var output1 = new AuthOutput("accessToken123", 3600L, null);
            var output2 = new AuthOutput("accessToken456", 3600L, null);

            assertThat(output1).isNotEqualTo(output2);
        }

        @Test
        @DisplayName("should exclude null isNewUser from JSON serialization")
        void shouldExcludeNullIsNewUserFromJson() throws Exception {
            var output = new AuthOutput("accessToken123", 3600L, null);
            var json = objectMapper.writeValueAsString(output);

            assertThat(json).doesNotContain("isNewUser");
            assertThat(json).contains("accessToken");
            assertThat(json).contains("expiresIn");
        }

        @Test
        @DisplayName("should include isNewUser in JSON serialization when not null")
        void shouldIncludeIsNewUserInJsonWhenNotNull() throws Exception {
            var output = new AuthOutput("accessToken123", 3600L, true);
            var json = objectMapper.writeValueAsString(output);

            assertThat(json).contains("isNewUser");
        }
    }
}
