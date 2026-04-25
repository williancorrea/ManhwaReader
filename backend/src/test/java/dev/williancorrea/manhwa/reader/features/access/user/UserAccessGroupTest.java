package dev.williancorrea.manhwa.reader.features.access.user;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroup;
import dev.williancorrea.manhwa.reader.features.access.group.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAccessGroup")
class UserAccessGroupTest {

  private UUID userId;
  private UUID groupId;
  private User user;
  private AccessGroup accessGroup;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    groupId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();
    accessGroup = AccessGroup.builder()
        .id(groupId)
        .name(GroupType.READER)
        .build();
  }

  @Nested
  @DisplayName("UserAccessGroup Builder")
  class UserAccessGroupBuilderTests {

    @Test
    @DisplayName("should create user access group with user and access group")
    void shouldCreateUserAccessGroupWithUserAndAccessGroup() {
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      assertThat(uag.getUser()).isEqualTo(user);
      assertThat(uag.getAccessGroup()).isEqualTo(accessGroup);
    }

    @Test
    @DisplayName("should create user access group with id")
    void shouldCreateUserAccessGroupWithId() {
      var compositeId = new UserAccessGroupId(userId, groupId);
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      assertThat(uag.getUser()).isEqualTo(user);
      assertThat(uag.getAccessGroup()).isEqualTo(accessGroup);
    }
  }

  @Nested
  @DisplayName("UserAccessGroup properties")
  class UserAccessGroupPropertiesTests {

    @Test
    @DisplayName("should support getters and setters")
    void shouldSupportGettersAndSetters() {
      var uag = new UserAccessGroup();
      uag.setUser(user);
      uag.setAccessGroup(accessGroup);

      assertThat(uag.getUser()).isEqualTo(user);
      assertThat(uag.getAccessGroup()).isEqualTo(accessGroup);
    }

    @Test
    @DisplayName("should support different user and access group combinations")
    void shouldSupportDifferentUserAndAccessGroupCombinations() {
      var user2 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();

      var uag1 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user2)
          .accessGroup(adminGroup)
          .build();

      assertThat(uag1.getUser()).isEqualTo(user);
      assertThat(uag1.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(uag2.getUser()).isEqualTo(user2);
      assertThat(uag2.getAccessGroup()).isEqualTo(adminGroup);
    }
  }

  @Nested
  @DisplayName("UserAccessGroup relationships")
  class UserAccessGroupRelationshipsTests {

    @Test
    @DisplayName("should establish relationship between user and access group")
    void shouldEstablishRelationshipBetweenUserAndAccessGroup() {
      var uag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      assertThat(uag.getUser().getName()).isEqualTo("Test User");
      assertThat(uag.getAccessGroup().getName()).isEqualTo(GroupType.READER);
    }

    @Test
    @DisplayName("should support user with multiple access groups")
    void shouldSupportUserWithMultipleAccessGroups() {
      var moderatorGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var uploaderGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();

      var uag1 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(moderatorGroup)
          .build();
      var uag3 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(uploaderGroup)
          .build();

      assertThat(uag1.getUser()).isEqualTo(user);
      assertThat(uag2.getUser()).isEqualTo(user);
      assertThat(uag3.getUser()).isEqualTo(user);
      assertThat(uag1.getAccessGroup()).isNotEqualTo(uag2.getAccessGroup());
      assertThat(uag2.getAccessGroup()).isNotEqualTo(uag3.getAccessGroup());
    }

    @Test
    @DisplayName("should support access group with multiple users")
    void shouldSupportAccessGroupWithMultipleUsers() {
      var user2 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var user3 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 3")
          .email("test3@example.com")
          .build();

      var uag1 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user2)
          .accessGroup(accessGroup)
          .build();
      var uag3 = UserAccessGroup.builder()
          .user(user3)
          .accessGroup(accessGroup)
          .build();

      assertThat(uag1.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(uag2.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(uag3.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(uag1.getUser()).isNotEqualTo(uag2.getUser());
      assertThat(uag2.getUser()).isNotEqualTo(uag3.getUser());
    }

    @Test
    @DisplayName("should support all group types for users")
    void shouldSupportAllGroupTypesForUsers() {
      for (var groupType : GroupType.values()) {
        var group = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(groupType)
            .build();
        var uag = UserAccessGroup.builder()
            .user(user)
            .accessGroup(group)
            .build();

        assertThat(uag.getAccessGroup().getName()).isEqualTo(groupType);
      }
    }
  }

  @Nested
  @DisplayName("UserAccessGroup all-args constructor")
  class UserAccessGroupAllArgsConstructorTests {

    @Test
    @DisplayName("should create user access group with all args constructor")
    void shouldCreateUserAccessGroupWithAllArgsConstructor() {
      var uag = new UserAccessGroup(user, accessGroup);

      assertThat(uag.getUser()).isEqualTo(user);
      assertThat(uag.getAccessGroup()).isEqualTo(accessGroup);
    }
  }

  @Nested
  @DisplayName("UserAccessGroup no-args constructor")
  class UserAccessGroupNoArgsConstructorTests {

    @Test
    @DisplayName("should create empty user access group with no args constructor")
    void shouldCreateEmptyUserAccessGroupWithNoArgsConstructor() {
      var uag = new UserAccessGroup();

      assertThat(uag.getUser()).isNull();
      assertThat(uag.getAccessGroup()).isNull();
    }
  }
}
