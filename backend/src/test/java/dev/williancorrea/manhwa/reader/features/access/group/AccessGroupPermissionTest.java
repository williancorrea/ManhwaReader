package dev.williancorrea.manhwa.reader.features.access.group;

import dev.williancorrea.manhwa.reader.features.access.permission.Permission;
import dev.williancorrea.manhwa.reader.features.access.permission.PermissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupPermission")
class AccessGroupPermissionTest {

  private UUID groupId;
  private UUID permissionId;
  private AccessGroup accessGroup;
  private Permission permission;

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
  }

  @Nested
  @DisplayName("AccessGroupPermission Builder")
  class AccessGroupPermissionBuilderTests {

    @Test
    @DisplayName("should create access group permission with group and permission")
    void shouldCreateAccessGroupPermissionWithGroupAndPermission() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      assertThat(agp.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp.getPermission()).isEqualTo(permission);
    }

    @Test
    @DisplayName("should create access group permission with accessGroup and permission")
    void shouldCreateAccessGroupPermissionWithAccessGroupAndPermission() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      assertThat(agp.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp.getPermission()).isEqualTo(permission);
    }
  }

  @Nested
  @DisplayName("AccessGroupPermission properties")
  class AccessGroupPermissionPropertiesTests {

    @Test
    @DisplayName("should support getters and setters")
    void shouldSupportGettersAndSetters() {
      var agp = new AccessGroupPermission();
      agp.setAccessGroup(accessGroup);
      agp.setPermission(permission);

      assertThat(agp.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp.getPermission()).isEqualTo(permission);
    }

    @Test
    @DisplayName("should support different group and permission combinations")
    void shouldSupportDifferentGroupAndPermissionCombinations() {
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var moderatorPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.MODERATOR)
          .build();

      var agp1 = AccessGroupPermission.builder()
          .accessGroup(adminGroup)
          .permission(permission)
          .build();
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(moderatorPermission)
          .build();

      assertThat(agp1.getAccessGroup()).isEqualTo(adminGroup);
      assertThat(agp1.getPermission()).isEqualTo(permission);
      assertThat(agp2.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp2.getPermission()).isEqualTo(moderatorPermission);
    }
  }

  @Nested
  @DisplayName("AccessGroupPermission relationships")
  class AccessGroupPermissionRelationshipsTests {

    @Test
    @DisplayName("should establish relationship between access group and permission")
    void shouldEstablishRelationshipBetweenAccessGroupAndPermission() {
      var agp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      assertThat(agp.getAccessGroup().getName()).isEqualTo(GroupType.READER);
      assertThat(agp.getPermission().getName()).isEqualTo(PermissionType.READER);
    }

    @Test
    @DisplayName("should support multiple permissions per access group")
    void shouldSupportMultiplePermissionsPerAccessGroup() {
      var readerPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.READER)
          .build();
      var uploaderPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.UPLOADER)
          .build();

      var agp1 = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(readerPermission)
          .build();
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(uploaderPermission)
          .build();

      assertThat(agp1.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp2.getAccessGroup()).isEqualTo(accessGroup);
      assertThat(agp1.getPermission()).isNotEqualTo(agp2.getPermission());
    }

    @Test
    @DisplayName("should support multiple access groups for same permission")
    void shouldSupportMultipleAccessGroupsForSamePermission() {
      var readerGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();
      var moderatorGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();

      var agp1 = AccessGroupPermission.builder()
          .accessGroup(readerGroup)
          .permission(permission)
          .build();
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(moderatorGroup)
          .permission(permission)
          .build();

      assertThat(agp1.getPermission()).isEqualTo(permission);
      assertThat(agp2.getPermission()).isEqualTo(permission);
      assertThat(agp1.getAccessGroup()).isNotEqualTo(agp2.getAccessGroup());
    }
  }
}
