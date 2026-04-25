package dev.williancorrea.manhwa.reader.features.access.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User")
class UserTest {

  private UUID userId;
  private String userName;
  private String userEmail;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userName = "Test User";
    userEmail = "test@example.com";
  }

  @Nested
  @DisplayName("hasPasswordLogin()")
  class HasPasswordLoginTests {

    @Test
    @DisplayName("should return true when passwordHash is not null")
    void shouldReturnTrueWhenPasswordHashNotNull() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .passwordHash("hashedPassword123")
          .build();

      assertThat(user.hasPasswordLogin()).isTrue();
    }

    @Test
    @DisplayName("should return false when passwordHash is null")
    void shouldReturnFalseWhenPasswordHashNull() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .build();

      assertThat(user.hasPasswordLogin()).isFalse();
    }
  }

  @Nested
  @DisplayName("hasGoogleLogin()")
  class HasGoogleLoginTests {

    @Test
    @DisplayName("should return true when googleId is not null")
    void shouldReturnTrueWhenGoogleIdNotNull() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .googleId("googleId123")
          .build();

      assertThat(user.hasGoogleLogin()).isTrue();
    }

    @Test
    @DisplayName("should return false when googleId is null")
    void shouldReturnFalseWhenGoogleIdNull() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .build();

      assertThat(user.hasGoogleLogin()).isFalse();
    }
  }

  @Nested
  @DisplayName("User Builder")
  class UserBuilderTests {

    @Test
    @DisplayName("should create user with all fields")
    void shouldCreateUserWithAllFields() {
      var now = OffsetDateTime.now();
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .passwordHash("hashedPassword")
          .googleId("googleId123")
          .avatarUrl("https://example.com/avatar.jpg")
          .emailVerified(true)
          .createdAt(now)
          .updatedAt(now)
          .build();

      assertThat(user.getId()).isEqualTo(userId);
      assertThat(user.getName()).isEqualTo(userName);
      assertThat(user.getEmail()).isEqualTo(userEmail);
      assertThat(user.getPasswordHash()).isEqualTo("hashedPassword");
      assertThat(user.getGoogleId()).isEqualTo("googleId123");
      assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/avatar.jpg");
      assertThat(user.isEmailVerified()).isTrue();
      assertThat(user.getCreatedAt()).isEqualTo(now);
      assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should create user with minimal fields")
    void shouldCreateUserWithMinimalFields() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .build();

      assertThat(user.getId()).isEqualTo(userId);
      assertThat(user.getName()).isEqualTo(userName);
      assertThat(user.getEmail()).isEqualTo(userEmail);
      assertThat(user.getPasswordHash()).isNull();
      assertThat(user.getGoogleId()).isNull();
      assertThat(user.getAvatarUrl()).isNull();
      assertThat(user.isEmailVerified()).isFalse();
    }
  }

  @Nested
  @DisplayName("User properties")
  class UserPropertiesTests {

    @Test
    @DisplayName("should support all getters and setters")
    void shouldSupportAllGettersAndSetters() {
      var user = new User();
      var now = OffsetDateTime.now();

      user.setId(userId);
      user.setName(userName);
      user.setEmail(userEmail);
      user.setPasswordHash("hashedPassword");
      user.setGoogleId("googleId123");
      user.setAvatarUrl("https://example.com/avatar.jpg");
      user.setEmailVerified(true);
      user.setCreatedAt(now);
      user.setUpdatedAt(now);

      assertThat(user.getId()).isEqualTo(userId);
      assertThat(user.getName()).isEqualTo(userName);
      assertThat(user.getEmail()).isEqualTo(userEmail);
      assertThat(user.getPasswordHash()).isEqualTo("hashedPassword");
      assertThat(user.getGoogleId()).isEqualTo("googleId123");
      assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/avatar.jpg");
      assertThat(user.isEmailVerified()).isTrue();
      assertThat(user.getCreatedAt()).isEqualTo(now);
      assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should have both password and google login")
    void shouldHaveBothLoginMethods() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .passwordHash("hashedPassword")
          .googleId("googleId123")
          .build();

      assertThat(user.hasPasswordLogin()).isTrue();
      assertThat(user.hasGoogleLogin()).isTrue();
    }

    @Test
    @DisplayName("should have only password login")
    void shouldHaveOnlyPasswordLogin() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .passwordHash("hashedPassword")
          .build();

      assertThat(user.hasPasswordLogin()).isTrue();
      assertThat(user.hasGoogleLogin()).isFalse();
    }

    @Test
    @DisplayName("should have only google login")
    void shouldHaveOnlyGoogleLogin() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .googleId("googleId123")
          .build();

      assertThat(user.hasPasswordLogin()).isFalse();
      assertThat(user.hasGoogleLogin()).isTrue();
    }

    @Test
    @DisplayName("should have neither password nor google login")
    void shouldHaveNeitherLogin() {
      var user = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .build();

      assertThat(user.hasPasswordLogin()).isFalse();
      assertThat(user.hasGoogleLogin()).isFalse();
    }
  }
}
