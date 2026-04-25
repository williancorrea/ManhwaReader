package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupPermissionId")
class AccessGroupPermissionIdTest {

  private UUID groupId;
  private UUID permissionId;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    permissionId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create composite id with group and permission ids")
    void shouldCreateCompositeIdWithGroupAndPermissionIds() {
      var id = new AccessGroupPermissionId(groupId, permissionId);

      assertThat(id.getAccessGroup()).isEqualTo(groupId);
      assertThat(id.getPermission()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should create empty composite id")
    void shouldCreateEmptyCompositeId() {
      var id = new AccessGroupPermissionId();

      assertThat(id.getAccessGroup()).isNull();
      assertThat(id.getPermission()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get group id")
    void shouldSetAndGetGroupId() {
      var id = new AccessGroupPermissionId();
      id.setAccessGroup(groupId);

      assertThat(id.getAccessGroup()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should set and get permission id")
    void shouldSetAndGetPermissionId() {
      var id = new AccessGroupPermissionId();
      id.setPermission(permissionId);

      assertThat(id.getPermission()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should set and get both ids")
    void shouldSetAndGetBothIds() {
      var id = new AccessGroupPermissionId();
      id.setAccessGroup(groupId);
      id.setPermission(permissionId);

      assertThat(id.getAccessGroup()).isEqualTo(groupId);
      assertThat(id.getPermission()).isEqualTo(permissionId);
    }
  }

  @Nested
  @DisplayName("Equality and Hashing")
  class EqualityAndHashingTests {

    @Test
    @DisplayName("should be equal when both ids are the same")
    void shouldBeEqualWhenBothIdsAreSame() {
      var id1 = new AccessGroupPermissionId(groupId, permissionId);
      var id2 = new AccessGroupPermissionId(groupId, permissionId);

      assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("should not be equal when group ids differ")
    void shouldNotBeEqualWhenGroupIdsDiffer() {
      var id1 = new AccessGroupPermissionId(groupId, permissionId);
      var id2 = new AccessGroupPermissionId(UUID.randomUUID(), permissionId);

      assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("should not be equal when permission ids differ")
    void shouldNotBeEqualWhenPermissionIdsDiffer() {
      var id1 = new AccessGroupPermissionId(groupId, permissionId);
      var id2 = new AccessGroupPermissionId(groupId, UUID.randomUUID());

      assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("should have same hash code when both ids are the same")
    void shouldHaveSameHashCodeWhenBothIdsAreSame() {
      var id1 = new AccessGroupPermissionId(groupId, permissionId);
      var id2 = new AccessGroupPermissionId(groupId, permissionId);

      assertThat(id1).hasSameHashCodeAs(id2);
    }

    @Test
    @DisplayName("should have different hash codes when ids differ")
    void shouldHaveDifferentHashCodesWhenIdsDiffer() {
      var id1 = new AccessGroupPermissionId(groupId, permissionId);
      var id2 = new AccessGroupPermissionId(UUID.randomUUID(), UUID.randomUUID());

      assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
  }
}
