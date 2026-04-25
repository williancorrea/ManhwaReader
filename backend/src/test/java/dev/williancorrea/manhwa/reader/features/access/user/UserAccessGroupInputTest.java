package dev.williancorrea.manhwa.reader.features.access.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAccessGroupInput")
class UserAccessGroupInputTest {

  private UUID userId;
  private UUID accessGroupId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    accessGroupId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create user access group input with user and access group ids")
    void shouldCreateUserAccessGroupInputWithIds() {
      var input = new UserAccessGroupInput(userId, accessGroupId);

      assertThat(input.getUserId()).isEqualTo(userId);
      assertThat(input.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should create empty user access group input with no-args constructor")
    void shouldCreateEmptyUserAccessGroupInputWithNoArgsConstructor() {
      var input = new UserAccessGroupInput();

      assertThat(input.getUserId()).isNull();
      assertThat(input.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get user id")
    void shouldSetAndGetUserId() {
      var input = new UserAccessGroupInput();
      input.setUserId(userId);

      assertThat(input.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should set and get access group id")
    void shouldSetAndGetAccessGroupId() {
      var input = new UserAccessGroupInput();
      input.setAccessGroupId(accessGroupId);

      assertThat(input.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should set and get both ids")
    void shouldSetAndGetBothIds() {
      var input = new UserAccessGroupInput();
      input.setUserId(userId);
      input.setAccessGroupId(accessGroupId);

      assertThat(input.getUserId()).isEqualTo(userId);
      assertThat(input.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should update ids independently")
    void shouldUpdateIdsIndependently() {
      var input = new UserAccessGroupInput(userId, accessGroupId);
      var newUserId = UUID.randomUUID();
      var newAccessGroupId = UUID.randomUUID();

      input.setUserId(newUserId);
      input.setAccessGroupId(newAccessGroupId);

      assertThat(input.getUserId()).isEqualTo(newUserId);
      assertThat(input.getAccessGroupId()).isEqualTo(newAccessGroupId);
    }
  }

  @Nested
  @DisplayName("All-args constructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create user access group input with different ids")
    void shouldCreateUserAccessGroupInputWithDifferentIds() {
      var user1Id = UUID.randomUUID();
      var group1Id = UUID.randomUUID();
      var user2Id = UUID.randomUUID();
      var group2Id = UUID.randomUUID();

      var input1 = new UserAccessGroupInput(user1Id, group1Id);
      var input2 = new UserAccessGroupInput(user2Id, group2Id);

      assertThat(input1.getUserId()).isNotEqualTo(input2.getUserId());
      assertThat(input1.getAccessGroupId()).isNotEqualTo(input2.getAccessGroupId());
    }

    @Test
    @DisplayName("should create user access group input with same ids")
    void shouldCreateUserAccessGroupInputWithSameIds() {
      var input1 = new UserAccessGroupInput(userId, accessGroupId);
      var input2 = new UserAccessGroupInput(userId, accessGroupId);

      assertThat(input1.getUserId()).isEqualTo(input2.getUserId());
      assertThat(input1.getAccessGroupId()).isEqualTo(input2.getAccessGroupId());
    }
  }

  @Nested
  @DisplayName("Field validation")
  class FieldValidationTests {

    @Test
    @DisplayName("should support null user id")
    void shouldSupportNullUserId() {
      var input = new UserAccessGroupInput(null, accessGroupId);

      assertThat(input.getUserId()).isNull();
      assertThat(input.getAccessGroupId()).isEqualTo(accessGroupId);
    }

    @Test
    @DisplayName("should support null access group id")
    void shouldSupportNullAccessGroupId() {
      var input = new UserAccessGroupInput(userId, null);

      assertThat(input.getUserId()).isEqualTo(userId);
      assertThat(input.getAccessGroupId()).isNull();
    }

    @Test
    @DisplayName("should support both ids being null")
    void shouldSupportBothIdsBeingNull() {
      var input = new UserAccessGroupInput(null, null);

      assertThat(input.getUserId()).isNull();
      assertThat(input.getAccessGroupId()).isNull();
    }

    @Test
    @DisplayName("should support setting ids to null after creation")
    void shouldSupportSettingIdsToNullAfterCreation() {
      var input = new UserAccessGroupInput(userId, accessGroupId);
      input.setUserId(null);
      input.setAccessGroupId(null);

      assertThat(input.getUserId()).isNull();
      assertThat(input.getAccessGroupId()).isNull();
    }
  }

  @Nested
  @DisplayName("Multiple instances")
  class MultipleInstancesTests {

    @Test
    @DisplayName("should maintain independent state for different instances")
    void shouldMaintainIndependentStateForDifferentInstances() {
      var user1 = UUID.randomUUID();
      var group1 = UUID.randomUUID();
      var user2 = UUID.randomUUID();
      var group2 = UUID.randomUUID();

      var input1 = new UserAccessGroupInput(user1, group1);
      var input2 = new UserAccessGroupInput(user2, group2);

      assertThat(input1.getUserId()).isEqualTo(user1);
      assertThat(input1.getAccessGroupId()).isEqualTo(group1);
      assertThat(input2.getUserId()).isEqualTo(user2);
      assertThat(input2.getAccessGroupId()).isEqualTo(group2);
    }

    @Test
    @DisplayName("should not affect other instances when modifying one")
    void shouldNotAffectOtherInstancesWhenModifyingOne() {
      var input1 = new UserAccessGroupInput(userId, accessGroupId);
      var input2 = new UserAccessGroupInput(userId, accessGroupId);

      var newUserId = UUID.randomUUID();
      input1.setUserId(newUserId);

      assertThat(input1.getUserId()).isEqualTo(newUserId);
      assertThat(input2.getUserId()).isEqualTo(userId);
    }
  }
}
