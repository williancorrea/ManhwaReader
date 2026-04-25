package dev.williancorrea.manhwa.reader.features.access.user;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroup;
import dev.williancorrea.manhwa.reader.features.access.group.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAccessGroupOutput")
class UserAccessGroupOutputTest {

  private UUID userId;
  private UUID accessGroupId;
  private User user;
  private AccessGroup accessGroup;
  private UserAccessGroup userAccessGroup;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    accessGroupId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();
    accessGroup = AccessGroup.builder()
        .id(accessGroupId)
        .name(GroupType.READER)
        .build();
    userAccessGroup = UserAccessGroup.builder()
        .user(user)
        .accessGroup(accessGroup)
        .build();
  }

  @Nested
  @DisplayName("Constructor with UserAccessGroup entity")
  class ConstructorWithEntityTests {

    @Test
    @DisplayName("should create user access group output from entity")
    void shouldCreateUserAccessGroupOutputFromEntity() {
      var output = new UserAccessGroupOutput(userAccessGroup);

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should handle entity with null user")
    void shouldHandleEntityWithNullUser() {
      var uag = UserAccessGroup.builder()
          .user(null)
          .accessGroup(accessGroup)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getUserId()).isNull();
      assertThat(output.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should handle entity with null access group")
    void shouldHandleEntityWithNullAccessGroup() {
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(null)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isNull();
    }

    @Test
    @DisplayName("should handle entity with both null user and access group")
    void shouldHandleEntityWithBothNull() {
      var uag = UserAccessGroup.builder()
          .user(null)
          .accessGroup(null)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getUserId()).isNull();
      assertThat(output.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("Constructor with entity and different group types")
  class ConstructorWithDifferentGroupTypesTests {

    @Test
    @DisplayName("should handle ADMINISTRATOR group type")
    void shouldHandleAdministratorGroupType() {
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(adminGroup)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isEqualTo(adminGroup.getId());
    }

    @Test
    @DisplayName("should handle MODERATOR group type")
    void shouldHandleModeratorGroupType() {
      var modGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(modGroup)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getAccessGroupId()).isEqualTo(modGroup.getId());
    }

    @Test
    @DisplayName("should handle UPLOADER group type")
    void shouldHandleUploaderGroupType() {
      var uploaderGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(uploaderGroup)
          .build();

      var output = new UserAccessGroupOutput(uag);

      assertThat(output.getAccessGroupId()).isEqualTo(uploaderGroup.getId());
    }

    @Test
    @DisplayName("should handle all group types")
    void shouldHandleAllGroupTypes() {
      for (var groupType : GroupType.values()) {
        var group = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(groupType)
            .build();
        var uag = UserAccessGroup.builder()
            .user(user)
            .accessGroup(group)
            .build();

        var output = new UserAccessGroupOutput(uag);

        assertThat(output.getUserId()).isEqualTo(userId);
        assertThat(output.getAccessGroupId()).isEqualTo(group.getId());
      }
    }
  }

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create user access group output with builder")
    void shouldCreateUserAccessGroupOutputWithBuilder() {
      var output = UserAccessGroupOutput.builder()
          .userId(userId)
          .accessGroupId(accessGroupId)
          .build();

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should create user access group output with partial builder")
    void shouldCreateUserAccessGroupOutputWithPartialBuilder() {
      var output = UserAccessGroupOutput.builder()
          .userId(userId)
          .build();

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isNull();
    }

    @Test
    @DisplayName("should create empty user access group output with builder")
    void shouldCreateEmptyUserAccessGroupOutputWithBuilder() {
      var output = UserAccessGroupOutput.builder().build();

      assertThat(output.getUserId()).isNull();
      assertThat(output.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("Getters")
  class GettersTests {

    @Test
    @DisplayName("should return user id via getter")
    void shouldReturnUserIdViaGetter() {
      var output = new UserAccessGroupOutput(userAccessGroup);

      assertThat(output.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should return access group id via getter")
    void shouldReturnAccessGroupIdViaGetter() {
      var output = new UserAccessGroupOutput(userAccessGroup);

      assertThat(output.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should return all fields via getters")
    void shouldReturnAllFieldsViaGetters() {
      var output = new UserAccessGroupOutput(userAccessGroup);

      assertThat(output.getUserId()).isNotNull();
      assertThat(output.getAccessGroupId()).isNotNull();
    }
  }

  @Nested
  @DisplayName("No-args constructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty user access group output with no-args constructor")
    void shouldCreateEmptyUserAccessGroupOutputWithNoArgsConstructor() {
      var output = new UserAccessGroupOutput();

      assertThat(output.getUserId()).isNull();
      assertThat(output.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("All-args constructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create user access group output with all args constructor")
    void shouldCreateUserAccessGroupOutputWithAllArgsConstructor() {
      var output = new UserAccessGroupOutput(userId, accessGroupId);

      assertThat(output.getUserId()).isEqualTo(userId);
      assertThat(output.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should create user access group output with null values")
    void shouldCreateUserAccessGroupOutputWithNullValues() {
      var output = new UserAccessGroupOutput(null, null);

      assertThat(output.getUserId()).isNull();
      assertThat(output.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var output = new UserAccessGroupOutput(userAccessGroup);

      assertThat(output).isInstanceOf(java.io.Serializable.class);
    }
  }

  @Nested
  @DisplayName("Multiple instances with different entities")
  class MultipleInstancesTests {

    @Test
    @DisplayName("should handle multiple user access group outputs")
    void shouldHandleMultipleUserAccessGroupOutputs() {
      var user2Id = UUID.randomUUID();
      var group2Id = UUID.randomUUID();
      var user2 = User.builder()
          .id(user2Id)
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var group2 = AccessGroup.builder()
          .id(group2Id)
          .name(GroupType.MODERATOR)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user2)
          .accessGroup(group2)
          .build();

      var output1 = new UserAccessGroupOutput(userAccessGroup);
      var output2 = new UserAccessGroupOutput(uag2);

      assertThat(output1.getUserId()).isEqualTo(userId);
      assertThat(output1.getAccessGroupId()).isEqualTo(accessGroupId);
      assertThat(output2.getUserId()).isEqualTo(user2Id);
      assertThat(output2.getAccessGroupId()).isEqualTo(group2Id);
    }
  }
}
