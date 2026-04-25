package dev.williancorrea.manhwa.reader.features.access.group;

import dev.williancorrea.manhwa.reader.features.access.permission.Permission;
import dev.williancorrea.manhwa.reader.features.access.permission.PermissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupPermissionOutput")
class AccessGroupPermissionOutputTest {

  private UUID groupId;
  private UUID permissionId;
  private AccessGroup accessGroup;
  private Permission permission;
  private AccessGroupPermission accessGroupPermission;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    permissionId = UUID.randomUUID();
    accessGroup = AccessGroup.builder()
        .id(groupId)
        .name(GroupType.READER)
        .build();
    permission = Permission.builder()
        .id(permissionId)
        .name(PermissionType.READER)
        .build();
    accessGroupPermission = AccessGroupPermission.builder()
        .accessGroup(accessGroup)
        .permission(permission)
        .build();
  }

  @Nested
  @DisplayName("Constructor from AccessGroupPermission")
  class ConstructorFromAccessGroupPermissionTests {

    @Test
    @DisplayName("should create output from access group permission")
    void shouldCreateOutputFromAccessGroupPermission() {
      var output = new AccessGroupPermissionOutput(accessGroupPermission);

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should extract group id from nested object")
    void shouldExtractGroupIdFromNestedObject() {
      var output = new AccessGroupPermissionOutput(accessGroupPermission);

      assertThat(output.getAccessGroupId()).isEqualTo(accessGroup.getId());
    }

    @Test
    @DisplayName("should extract permission id from nested object")
    void shouldExtractPermissionIdFromNestedObject() {
      var output = new AccessGroupPermissionOutput(accessGroupPermission);

      assertThat(output.getPermissionId()).isEqualTo(permission.getId());
    }

    @Test
    @DisplayName("should handle null access group")
    void shouldHandleNullAccessGroup() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(null)
          .permission(permission)
          .build();

      var output = new AccessGroupPermissionOutput(agp);

      assertThat(output.getAccessGroupId()).isNull();
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should handle null permission")
    void shouldHandleNullPermission() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(null)
          .build();

      var output = new AccessGroupPermissionOutput(agp);

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isNull();
    }

    @Test
    @DisplayName("should handle both null access group and permission")
    void shouldHandleBothNullAccessGroupAndPermission() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(null)
          .permission(null)
          .build();

      var output = new AccessGroupPermissionOutput(agp);

      assertThat(output.getAccessGroupId()).isNull();
      assertThat(output.getPermissionId()).isNull();
    }
  }

  @Nested
  @DisplayName("Builder Constructor")
  class BuilderConstructorTests {

    @Test
    @DisplayName("should create output using builder")
    void shouldCreateOutputUsingBuilder() {
      var output = AccessGroupPermissionOutput.builder()
          .accessGroupId(groupId)
          .permissionId(permissionId)
          .build();

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should create output with only access group id")
    void shouldCreateOutputWithOnlyAccessGroupId() {
      var output = AccessGroupPermissionOutput.builder()
          .accessGroupId(groupId)
          .build();

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isNull();
    }

    @Test
    @DisplayName("should create output with only permission id")
    void shouldCreateOutputWithOnlyPermissionId() {
      var output = AccessGroupPermissionOutput.builder()
          .permissionId(permissionId)
          .build();

      assertThat(output.getAccessGroupId()).isNull();
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create output with all args")
    void shouldCreateOutputWithAllArgs() {
      var output = new AccessGroupPermissionOutput(groupId, permissionId);

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }
  }

  @Nested
  @DisplayName("NoArgsConstructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty output")
    void shouldCreateEmptyOutput() {
      var output = new AccessGroupPermissionOutput();

      assertThat(output.getAccessGroupId()).isNull();
      assertThat(output.getPermissionId()).isNull();
    }
  }

  @Nested
  @DisplayName("Getters")
  class GettersTests {

    @Test
    @DisplayName("should get access group id")
    void shouldGetAccessGroupId() {
      var output = new AccessGroupPermissionOutput(groupId, permissionId);

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should get permission id")
    void shouldGetPermissionId() {
      var output = new AccessGroupPermissionOutput(groupId, permissionId);

      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var output = new AccessGroupPermissionOutput(groupId, permissionId);

      assertThat(output).isInstanceOf(java.io.Serializable.class);
    }
  }

  @Nested
  @DisplayName("Multiple Conversions")
  class MultipleConversionsTests {

    @Test
    @DisplayName("should convert multiple access group permissions correctly")
    void shouldConvertMultipleAccessGroupPermissionsCorrectly() {
      var group1 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var group2 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var permission1 = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.ADMINISTRATOR)
          .build();
      var permission2 = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.MODERATOR)
          .build();

      var agp1 = AccessGroupPermission.builder()
          .accessGroup(group1)
          .permission(permission1)
          .build();
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(group2)
          .permission(permission2)
          .build();

      var output1 = new AccessGroupPermissionOutput(agp1);
      var output2 = new AccessGroupPermissionOutput(agp2);

      assertThat(output1.getAccessGroupId()).isEqualTo(group1.getId());
      assertThat(output1.getPermissionId()).isEqualTo(permission1.getId());
      assertThat(output2.getAccessGroupId()).isEqualTo(group2.getId());
      assertThat(output2.getPermissionId()).isEqualTo(permission2.getId());
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCasesTests {

    @Test
    @DisplayName("should handle same group and permission ids")
    void shouldHandleSameGroupAndPermissionIds() {
      var sameId = UUID.randomUUID();
      var output = new AccessGroupPermissionOutput(sameId, sameId);

      assertThat(output.getAccessGroupId()).isEqualTo(sameId);
      assertThat(output.getPermissionId()).isEqualTo(sameId);
      assertThat(output.getAccessGroupId()).isEqualTo(output.getPermissionId());
    }

    @Test
    @DisplayName("should handle group with null id")
    void shouldHandleGroupWithNullId() {
      var groupWithNullId = AccessGroup.builder()
          .id(null)
          .name(GroupType.READER)
          .build();
      var agp = AccessGroupPermission.builder()
          .accessGroup(groupWithNullId)
          .permission(permission)
          .build();

      var output = new AccessGroupPermissionOutput(agp);

      assertThat(output.getAccessGroupId()).isNull();
      assertThat(output.getPermissionId()).isEqualTo(permissionId);
    }

    @Test
    @DisplayName("should handle permission with null id")
    void shouldHandlePermissionWithNullId() {
      var permissionWithNullId = Permission.builder()
          .id(null)
          .name(PermissionType.READER)
          .build();
      var agp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permissionWithNullId)
          .build();

      var output = new AccessGroupPermissionOutput(agp);

      assertThat(output.getAccessGroupId()).isEqualTo(groupId);
      assertThat(output.getPermissionId()).isNull();
    }
  }
}
