package dev.williancorrea.manhwa.reader.features.access.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Permission")
class PermissionTest {

  private UUID permissionId;

  @BeforeEach
  void setUp() {
    permissionId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("Permission Builder")
  class PermissionBuilderTests {

    @Test
    @DisplayName("should create permission with id and name")
    void shouldCreatePermissionWithIdAndName() {
      var permission = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();

      assertThat(permission.getId()).isEqualTo(permissionId);
      assertThat(permission.getName()).isEqualTo(PermissionType.READER);
    }

    @Test
    @DisplayName("should create permission with all permission types")
    void shouldCreatePermissionWithAllPermissionTypes() {
      for (var type : PermissionType.values()) {
        var permission = Permission.builder()
            .id(UUID.randomUUID())
            .name(type)
            .build();

        assertThat(permission.getName()).isEqualTo(type);
      }
    }
  }

  @Nested
  @DisplayName("Permission properties")
  class PermissionPropertiesTests {

    @Test
    @DisplayName("should support getters and setters")
    void shouldSupportGettersAndSetters() {
      var permission = new Permission();
      permission.setId(permissionId);
      permission.setName(PermissionType.ADMINISTRATOR);

      assertThat(permission.getId()).isEqualTo(permissionId);
      assertThat(permission.getName()).isEqualTo(PermissionType.ADMINISTRATOR);
    }

    @Test
    @DisplayName("should support all permission types")
    void shouldSupportAllPermissionTypes() {
      var adminPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.ADMINISTRATOR)
          .build();
      var moderatorPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.MODERATOR)
          .build();
      var readerPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.READER)
          .build();
      var uploaderPermission = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.UPLOADER)
          .build();

      assertThat(adminPermission.getName()).isEqualTo(PermissionType.ADMINISTRATOR);
      assertThat(moderatorPermission.getName()).isEqualTo(PermissionType.MODERATOR);
      assertThat(readerPermission.getName()).isEqualTo(PermissionType.READER);
      assertThat(uploaderPermission.getName()).isEqualTo(PermissionType.UPLOADER);
    }
  }

  @Nested
  @DisplayName("Permission equality")
  class PermissionEqualityTests {

    @Test
    @DisplayName("should be equal when ids are the same")
    void shouldBeEqualWhenIdsAreSame() {
      var permission1 = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();
      var permission2 = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();

      assertThat(permission1).isEqualTo(permission2);
    }

    @Test
    @DisplayName("should not be equal when ids differ")
    void shouldNotBeEqualWhenIdsDiffer() {
      var permission1 = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();
      var permission2 = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.READER)
          .build();

      assertThat(permission1).isNotEqualTo(permission2);
    }

    @Test
    @DisplayName("should have same hash code when ids are the same")
    void shouldHaveSameHashCodeWhenIdsAreSame() {
      var permission1 = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();
      var permission2 = Permission.builder()
          .id(permissionId)
          .name(PermissionType.READER)
          .build();

      assertThat(permission1).hasSameHashCodeAs(permission2);
    }
  }
}
