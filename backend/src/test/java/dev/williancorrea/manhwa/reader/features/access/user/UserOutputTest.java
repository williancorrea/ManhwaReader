package dev.williancorrea.manhwa.reader.features.access.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserOutput")
class UserOutputTest {

  private UUID userId;
  private String userName;
  private String userEmail;
  private String avatarUrl;
  private OffsetDateTime createdAt;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userName = "Test User";
    userEmail = "test@example.com";
    avatarUrl = "https://example.com/avatar.jpg";
    createdAt = OffsetDateTime.now();
    user = User.builder()
        .id(userId)
        .name(userName)
        .email(userEmail)
        .avatarUrl(avatarUrl)
        .createdAt(createdAt)
        .build();
  }

  @Nested
  @DisplayName("Constructor with User entity")
  class ConstructorWithUserTests {

    @Test
    @DisplayName("should create user output from user entity")
    void shouldCreateUserOutputFromUserEntity() {
      var output = new UserOutput(user);

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isEqualTo(avatarUrl);
      assertThat(output.getCreatedAt()).isEqualTo(createdAt);
      assertThat(output.getRoles()).isNull();
    }

    @Test
    @DisplayName("should handle user with null avatar url")
    void shouldHandleUserWithNullAvatarUrl() {
      var userWithoutAvatar = User.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .createdAt(createdAt)
          .build();

      var output = new UserOutput(userWithoutAvatar);

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isNull();
      assertThat(output.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should copy user id correctly")
    void shouldCopyUserIdCorrectly() {
      var output = new UserOutput(user);

      assertThat(output.getId()).isEqualTo(user.getId());
    }
  }

  @Nested
  @DisplayName("Constructor with User entity and roles")
  class ConstructorWithUserAndRolesTests {

    @Test
    @DisplayName("should create user output with roles")
    void shouldCreateUserOutputWithRoles() {
      var roles = List.of("READER", "MODERATOR");

      var output = new UserOutput(user, roles);

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isEqualTo(avatarUrl);
      assertThat(output.getCreatedAt()).isEqualTo(createdAt);
      assertThat(output.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("should handle empty roles list")
    void shouldHandleEmptyRolesList() {
      var roles = List.<String>of();

      var output = new UserOutput(user, roles);

      assertThat(output.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("should handle single role")
    void shouldHandleSingleRole() {
      var roles = List.of("ADMINISTRATOR");

      var output = new UserOutput(user, roles);

      assertThat(output.getRoles()).hasSize(1).contains("ADMINISTRATOR");
    }

    @Test
    @DisplayName("should handle multiple roles")
    void shouldHandleMultipleRoles() {
      var roles = List.of("ADMINISTRATOR", "MODERATOR", "UPLOADER", "READER");

      var output = new UserOutput(user, roles);

      assertThat(output.getRoles()).hasSize(4).contains("ADMINISTRATOR", "MODERATOR", "UPLOADER", "READER");
    }
  }

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create user output with builder")
    void shouldCreateUserOutputWithBuilder() {
      var roles = List.of("READER");

      var output = UserOutput.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .avatarUrl(avatarUrl)
          .createdAt(createdAt)
          .roles(roles)
          .build();

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isEqualTo(avatarUrl);
      assertThat(output.getCreatedAt()).isEqualTo(createdAt);
      assertThat(output.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("should create user output with partial builder")
    void shouldCreateUserOutputWithPartialBuilder() {
      var output = UserOutput.builder()
          .id(userId)
          .name(userName)
          .email(userEmail)
          .build();

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isNull();
      assertThat(output.getCreatedAt()).isNull();
      assertThat(output.getRoles()).isNull();
    }
  }

  @Nested
  @DisplayName("Getters")
  class GettersTests {

    @Test
    @DisplayName("should return all fields via getters")
    void shouldReturnAllFieldsViaGetters() {
      var roles = List.of("READER", "MODERATOR");
      var output = new UserOutput(user, roles);

      assertThat(output.getId()).isNotNull();
      assertThat(output.getName()).isNotNull();
      assertThat(output.getEmail()).isNotNull();
      assertThat(output.getAvatarUrl()).isNotNull();
      assertThat(output.getCreatedAt()).isNotNull();
      assertThat(output.getRoles()).isNotNull();
    }

    @Test
    @DisplayName("should return correct field values")
    void shouldReturnCorrectFieldValues() {
      var output = new UserOutput(user);

      assertThat(output.getId()).isEqualTo(user.getId());
      assertThat(output.getName()).isEqualTo(user.getName());
      assertThat(output.getEmail()).isEqualTo(user.getEmail());
      assertThat(output.getAvatarUrl()).isEqualTo(user.getAvatarUrl());
      assertThat(output.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("No-args constructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty user output with no-args constructor")
    void shouldCreateEmptyUserOutputWithNoArgsConstructor() {
      var output = new UserOutput();

      assertThat(output.getId()).isNull();
      assertThat(output.getName()).isNull();
      assertThat(output.getEmail()).isNull();
      assertThat(output.getAvatarUrl()).isNull();
      assertThat(output.getCreatedAt()).isNull();
      assertThat(output.getRoles()).isNull();
    }
  }

  @Nested
  @DisplayName("All-args constructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create user output with all args constructor")
    void shouldCreateUserOutputWithAllArgsConstructor() {
      var roles = List.of("READER");

      var output = new UserOutput(userId, userName, userEmail, avatarUrl, createdAt, roles);

      assertThat(output.getId()).isEqualTo(userId);
      assertThat(output.getName()).isEqualTo(userName);
      assertThat(output.getEmail()).isEqualTo(userEmail);
      assertThat(output.getAvatarUrl()).isEqualTo(avatarUrl);
      assertThat(output.getCreatedAt()).isEqualTo(createdAt);
      assertThat(output.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("should create user output with null avatar url")
    void shouldCreateUserOutputWithNullAvatarUrl() {
      var output = new UserOutput(userId, userName, userEmail, null, createdAt, null);

      assertThat(output.getAvatarUrl()).isNull();
      assertThat(output.getRoles()).isNull();
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var output = new UserOutput(user);

      assertThat(output).isInstanceOf(java.io.Serializable.class);
    }
  }
}
