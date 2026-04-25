package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupPermissionInput")
class AccessGroupPermissionInputTest {

  private UUID groupId;
  private UUID permissionId;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    permissionId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create input with group and permission ids")
    void shouldCreateInputWithGroupAndPermissionIds() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);

      assertThat(input.getAccessGroupId()).isEqualTo(groupId);
      assertThat(input.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should create input with multiple different ids")
    void shouldCreateInputWithMultipleDifferentIds() {
      var group1 = UUID.randomUUID();
      var perm1 = UUID.randomUUID();
      var group2 = UUID.randomUUID();
      var perm2 = UUID.randomUUID();

      var input1 = new AccessGroupPermissionInput(group1, perm1);
      var input2 = new AccessGroupPermissionInput(group2, perm2);

      assertThat(input1.getAccessGroupId()).isEqualTo(group1);
      assertThat(input1.getPermissionId()).isEqualTo(perm1);
      assertThat(input2.getAccessGroupId()).isEqualTo(group2);
      assertThat(input2.getPermissionId()).isEqualTo(perm2);
    }
  }

  @Nested
  @DisplayName("NoArgsConstructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty input")
    void shouldCreateEmptyInput() {
      var input = new AccessGroupPermissionInput();

      assertThat(input.getAccessGroupId()).isNull();
      assertThat(input.getPermissionId()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get access group id")
    void shouldSetAndGetAccessGroupId() {
      var input = new AccessGroupPermissionInput();
      input.setAccessGroupId(groupId);

      assertThat(input.getAccessGroupId()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should set and get permission id")
    void shouldSetAndGetPermissionId() {
      var input = new AccessGroupPermissionInput();
      input.setPermissionId(permissionId);

      assertThat(input.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should set and get both ids")
    void shouldSetAndGetBothIds() {
      var input = new AccessGroupPermissionInput();
      input.setAccessGroupId(groupId);
      input.setPermissionId(permissionId);

      assertThat(input.getAccessGroupId()).isEqualTo(groupId);
      assertThat(input.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should update ids")
    void shouldUpdateIds() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);
      var newGroupId = UUID.randomUUID();
      var newPermissionId = UUID.randomUUID();

      input.setAccessGroupId(newGroupId);
      input.setPermissionId(newPermissionId);

      assertThat(input.getAccessGroupId()).isEqualTo(newGroupId);
      assertThat(input.getPermissionId()).isEqualTo(newPermissionId);
    }

    @Test
    @DisplayName("should set ids to null")
    void shouldSetIdsToNull() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);
      input.setAccessGroupId(null);
      input.setPermissionId(null);

      assertThat(input.getAccessGroupId()).isNull();
      assertThat(input.getPermissionId()).isNull();
    }
  }

  @Nested
  @DisplayName("ID Validation")
  class IDValidationTests {

    @Test
    @DisplayName("should accept valid UUIDs")
    void shouldAcceptValidUUIDs() {
      var validIds = new UUID[]{
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID()
      };

      for (var id : validIds) {
        var input = new AccessGroupPermissionInput(id, id);

        assertThat(input.getAccessGroupId()).isEqualTo(id);
        assertThat(input.getPermissionId()).isEqualTo(id);
      }
    }

    @Test
    @DisplayName("should accept same id for both group and permission")
    void shouldAcceptSameIdForBothGroupAndPermission() {
      var sameId = UUID.randomUUID();
      var input = new AccessGroupPermissionInput(sameId, sameId);

      assertThat(input.getAccessGroupId()).isEqualTo(sameId);
      assertThat(input.getPermissionId()).isEqualTo(sameId);
    }

    @Test
    @DisplayName("should accept different ids")
    void shouldAcceptDifferentIds() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);

      assertThat(input.getAccessGroupId()).isNotEqualTo(input.getPermissionId());
    }

    @Test
    @DisplayName("should accept null access group id")
    void shouldAcceptNullAccessGroupId() {
      var input = new AccessGroupPermissionInput(null, permissionId);

      assertThat(input.getAccessGroupId()).isNull();
      assertThat(input.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should accept null permission id")
    void shouldAcceptNullPermissionId() {
      var input = new AccessGroupPermissionInput(groupId, null);

      assertThat(input.getAccessGroupId()).isEqualTo(groupId);
      assertThat(input.getPermissionId()).isNull();
    }

    @Test
    @DisplayName("should accept both null ids")
    void shouldAcceptBothNullIds() {
      var input = new AccessGroupPermissionInput(null, null);

      assertThat(input.getAccessGroupId()).isNull();
      assertThat(input.getPermissionId()).isNull();
    }
  }

  @Nested
  @DisplayName("Multiple Instances")
  class MultipleInstancesTests {

    @Test
    @DisplayName("should create multiple independent instances")
    void shouldCreateMultipleIndependentInstances() {
      var input1 = new AccessGroupPermissionInput(groupId, permissionId);
      var input2 = new AccessGroupPermissionInput(UUID.randomUUID(), UUID.randomUUID());
      var input3 = new AccessGroupPermissionInput(UUID.randomUUID(), UUID.randomUUID());

      assertThat(input1.getAccessGroupId()).isEqualTo(groupId);
      assertThat(input1.getPermissionId()).isEqualTo(permissionId);
      assertThat(input2.getAccessGroupId()).isNotEqualTo(input1.getAccessGroupId());
      assertThat(input3.getAccessGroupId()).isNotEqualTo(input1.getAccessGroupId());
    }

    @Test
    @DisplayName("should modify instances independently")
    void shouldModifyInstancesIndependently() {
      var input1 = new AccessGroupPermissionInput(groupId, permissionId);
      var input2 = new AccessGroupPermissionInput(groupId, permissionId);

      var newGroupId = UUID.randomUUID();
      input1.setAccessGroupId(newGroupId);

      assertThat(input1.getAccessGroupId()).isEqualTo(newGroupId);
      assertThat(input2.getAccessGroupId()).isEqualTo(groupId);
    }
  }

  @Nested
  @DisplayName("Immutability Simulation")
  class ImmutabilitySimulationTests {

    @Test
    @DisplayName("should support replacement pattern")
    void shouldSupportReplacementPattern() {
      var input1 = new AccessGroupPermissionInput(groupId, permissionId);
      var newGroupId = UUID.randomUUID();

      var input2 = new AccessGroupPermissionInput(newGroupId, input1.getPermissionId());

      assertThat(input1.getAccessGroupId()).isEqualTo(groupId);
      assertThat(input2.getAccessGroupId()).isEqualTo(newGroupId);
    }
  }
}
