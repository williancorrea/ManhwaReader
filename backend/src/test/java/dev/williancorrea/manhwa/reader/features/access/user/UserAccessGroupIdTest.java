package dev.williancorrea.manhwa.reader.features.access.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAccessGroupId")
class UserAccessGroupIdTest {

  private UUID userId;
  private UUID groupId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    groupId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create composite id with user and group ids")
    void shouldCreateCompositeIdWithUserAndGroupIds() {
      var id = new UserAccessGroupId(userId, groupId);

      assertThat(id.getUser()).isEqualTo(userId);
      assertThat(id.getAccessGroup()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should create empty composite id")
    void shouldCreateEmptyCompositeId() {
      var id = new UserAccessGroupId();

      assertThat(id.getUser()).isNull();
      assertThat(id.getAccessGroup()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get user id")
    void shouldSetAndGetUserId() {
      var id = new UserAccessGroupId();
      id.setUser(userId);

      assertThat(id.getUser()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should set and get group id")
    void shouldSetAndGetGroupId() {
      var id = new UserAccessGroupId();
      id.setAccessGroup(groupId);

      assertThat(id.getAccessGroup()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should set and get both ids")
    void shouldSetAndGetBothIds() {
      var id = new UserAccessGroupId();
      id.setUser(userId);
      id.setAccessGroup(groupId);

      assertThat(id.getUser()).isEqualTo(userId);
      assertThat(id.getAccessGroup()).isEqualTo(groupId);
    }
  }

  @Nested
  @DisplayName("Equality and Hashing")
  class EqualityAndHashingTests {

    @Test
    @DisplayName("should be equal when both ids are the same")
    void shouldBeEqualWhenBothIdsAreSame() {
      var id1 = new UserAccessGroupId(userId, groupId);
      var id2 = new UserAccessGroupId(userId, groupId);

      assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("should not be equal when user ids differ")
    void shouldNotBeEqualWhenUserIdsDiffer() {
      var id1 = new UserAccessGroupId(userId, groupId);
      var id2 = new UserAccessGroupId(UUID.randomUUID(), groupId);

      assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("should not be equal when group ids differ")
    void shouldNotBeEqualWhenGroupIdsDiffer() {
      var id1 = new UserAccessGroupId(userId, groupId);
      var id2 = new UserAccessGroupId(userId, UUID.randomUUID());

      assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("should have same hash code when both ids are the same")
    void shouldHaveSameHashCodeWhenBothIdsAreSame() {
      var id1 = new UserAccessGroupId(userId, groupId);
      var id2 = new UserAccessGroupId(userId, groupId);

      assertThat(id1).hasSameHashCodeAs(id2);
    }

    @Test
    @DisplayName("should have different hash codes when ids differ")
    void shouldHaveDifferentHashCodesWhenIdsDiffer() {
      var id1 = new UserAccessGroupId(userId, groupId);
      var id2 = new UserAccessGroupId(UUID.randomUUID(), UUID.randomUUID());

      assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
  }
}
