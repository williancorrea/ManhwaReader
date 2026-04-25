package dev.williancorrea.manhwa.reader.features.access.group;

import dev.williancorrea.manhwa.reader.features.access.permission.Permission;
import dev.williancorrea.manhwa.reader.features.access.permission.PermissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessGroupPermissionResource")
class AccessGroupPermissionResourceTest {

  @Mock
  private AccessGroupPermissionService accessGroupPermissionService;

  @InjectMocks
  private AccessGroupPermissionResource accessGroupPermissionResource;

  private UUID groupId;
  private UUID permissionId;
  private AccessGroupPermissionId compositeId;
  private AccessGroup accessGroup;
  private Permission permission;
  private AccessGroupPermission accessGroupPermission;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    permissionId = UUID.randomUUID();
    compositeId = new AccessGroupPermissionId(groupId, permissionId);
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
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all access group permissions")
    void shouldReturnAllAccessGroupPermissions() {
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(AccessGroup.builder()
              .id(UUID.randomUUID())
              .name(GroupType.MODERATOR)
              .build())
          .permission(Permission.builder()
              .id(UUID.randomUUID())
              .name(PermissionType.MODERATOR)
              .build())
          .build();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionService.findAll()).thenReturn(permissions);

      var response = accessGroupPermissionResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(accessGroupPermissionService).findAll();
    }

    @Test
    @DisplayName("should return empty list when no permissions exist")
    void shouldReturnEmptyListWhenNoPermissionsExist() {
      when(accessGroupPermissionService.findAll()).thenReturn(List.of());

      var response = accessGroupPermissionResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(accessGroupPermissionService).findAll();
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var permissions = List.of(accessGroupPermission);

      when(accessGroupPermissionService.findAll()).thenReturn(permissions);

      var response = accessGroupPermissionResource.findAll();

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> {
            assertThat(body.get(0).getAccessGroupId()).isEqualTo(groupId);
            assertThat(body.get(0).getPermissionId()).isEqualTo(permissionId);
          });
      verify(accessGroupPermissionService).findAll();
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create and return access group permission")
    void shouldCreateAndReturnAccessGroupPermission() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);

      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(accessGroupPermission);

      var response = accessGroupPermissionResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
            assertThat(body.getPermissionId()).isEqualTo(permissionId);
          });
      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
    }

    @Test
    @DisplayName("should convert input to entity")
    void shouldConvertInputToEntity() {
      var newGroupId = UUID.randomUUID();
      var newPermissionId = UUID.randomUUID();
      var input = new AccessGroupPermissionInput(newGroupId, newPermissionId);
      var savedAgp = AccessGroupPermission.builder()
          .accessGroup(AccessGroup.builder().id(newGroupId).build())
          .permission(Permission.builder().id(newPermissionId).build())
          .build();

      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(savedAgp);

      var response = accessGroupPermissionResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return access group permission when found")
    void shouldReturnAccessGroupPermissionWhenFound() {
      when(accessGroupPermissionService.findById(compositeId))
          .thenReturn(Optional.of(accessGroupPermission));

      var response = accessGroupPermissionResource.findById(groupId, permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
            assertThat(body.getPermissionId()).isEqualTo(permissionId);
          });
      verify(accessGroupPermissionService).findById(any(AccessGroupPermissionId.class));
    }

    @Test
    @DisplayName("should return not found when permission does not exist")
    void shouldReturnNotFoundWhenPermissionDoesNotExist() {
      when(accessGroupPermissionService.findById(compositeId))
          .thenReturn(Optional.empty());

      var response = accessGroupPermissionResource.findById(groupId, permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupPermissionService).findById(any(AccessGroupPermissionId.class));
    }

    @Test
    @DisplayName("should convert entity to output DTO")
    void shouldConvertEntityToOutputDTO() {
      when(accessGroupPermissionService.findById(compositeId))
          .thenReturn(Optional.of(accessGroupPermission));

      var response = accessGroupPermissionResource.findById(groupId, permissionId);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getAccessGroupId()).isEqualTo(accessGroup.getId());
            assertThat(body.getPermissionId()).isEqualTo(permission.getId());
          });
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update and return access group permission")
    void shouldUpdateAndReturnAccessGroupPermission() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);
      var updatedAgp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(true);
      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(updatedAgp);

      var response = accessGroupPermissionResource.update(groupId, permissionId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
            assertThat(body.getPermissionId()).isEqualTo(permissionId);
          });
      verify(accessGroupPermissionService).existsById(any(AccessGroupPermissionId.class));
      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
    }

    @Test
    @DisplayName("should return not found when permission does not exist")
    void shouldReturnNotFoundWhenPermissionDoesNotExist() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);

      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(false);

      var response = accessGroupPermissionResource.update(groupId, permissionId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupPermissionService).existsById(any(AccessGroupPermissionId.class));
    }

    @Test
    @DisplayName("should preserve ids during update")
    void shouldPreserveIdsDuringUpdate() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);
      var updatedAgp = AccessGroupPermission.builder()
          .accessGroup(AccessGroup.builder().id(groupId).build())
          .permission(Permission.builder().id(permissionId).build())
          .build();

      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(true);
      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(updatedAgp);

      var response = accessGroupPermissionResource.update(groupId, permissionId, input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
            assertThat(body.getPermissionId()).isEqualTo(permissionId);
          });
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete access group permission successfully")
    void shouldDeleteAccessGroupPermissionSuccessfully() {
      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(true);

      var response = accessGroupPermissionResource.delete(groupId, permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(accessGroupPermissionService).existsById(any(AccessGroupPermissionId.class));
      verify(accessGroupPermissionService).deleteById(any(AccessGroupPermissionId.class));
    }

    @Test
    @DisplayName("should return not found when permission does not exist")
    void shouldReturnNotFoundWhenPermissionDoesNotExist() {
      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(false);

      var response = accessGroupPermissionResource.delete(groupId, permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupPermissionService).existsById(any(AccessGroupPermissionId.class));
    }

    @Test
    @DisplayName("should not call deleteById if permission does not exist")
    void shouldNotCallDeleteByIdIfPermissionDoesNotExist() {
      when(accessGroupPermissionService.existsById(compositeId)).thenReturn(false);

      accessGroupPermissionResource.delete(groupId, permissionId);

      verify(accessGroupPermissionService).existsById(any(AccessGroupPermissionId.class));
    }
  }

  @Nested
  @DisplayName("findAllByAccessGroup()")
  class FindAllByAccessGroupTests {

    @Test
    @DisplayName("should return all permissions for access group")
    void shouldReturnAllPermissionsForAccessGroup() {
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(Permission.builder()
              .id(UUID.randomUUID())
              .name(PermissionType.UPLOADER)
              .build())
          .build();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionService.findAllByAccessGroupId(groupId))
          .thenReturn(permissions);

      var response = accessGroupPermissionResource.findAllByAccessGroup(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2)
          .allSatisfy(output -> {
            assertThat(output.getAccessGroupId()).isEqualTo(groupId);
          });
      verify(accessGroupPermissionService).findAllByAccessGroupId(groupId);
    }

    @Test
    @DisplayName("should return empty list when access group has no permissions")
    void shouldReturnEmptyListWhenAccessGroupHasNoPermissions() {
      when(accessGroupPermissionService.findAllByAccessGroupId(groupId))
          .thenReturn(List.of());

      var response = accessGroupPermissionResource.findAllByAccessGroup(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(accessGroupPermissionService).findAllByAccessGroupId(groupId);
    }
  }

  @Nested
  @DisplayName("findAllByPermission()")
  class FindAllByPermissionTests {

    @Test
    @DisplayName("should return all access groups for permission")
    void shouldReturnAllAccessGroupsForPermission() {
      var agp2 = AccessGroupPermission.builder()
          .accessGroup(AccessGroup.builder()
              .id(UUID.randomUUID())
              .name(GroupType.MODERATOR)
              .build())
          .permission(permission)
          .build();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionService.findAllByPermissionId(permissionId))
          .thenReturn(permissions);

      var response = accessGroupPermissionResource.findAllByPermission(permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2)
          .allSatisfy(output -> {
            assertThat(output.getPermissionId()).isEqualTo(permissionId);
          });
      verify(accessGroupPermissionService).findAllByPermissionId(permissionId);
    }

    @Test
    @DisplayName("should return empty list when permission has no access groups")
    void shouldReturnEmptyListWhenPermissionHasNoAccessGroups() {
      when(accessGroupPermissionService.findAllByPermissionId(permissionId))
          .thenReturn(List.of());

      var response = accessGroupPermissionResource.findAllByPermission(permissionId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(accessGroupPermissionService).findAllByPermissionId(permissionId);
    }
  }

  @Nested
  @DisplayName("toEntity()")
  class ToEntityTests {

    @Test
    @DisplayName("should convert input to entity correctly")
    void shouldConvertInputToEntityCorrectly() {
      var input = new AccessGroupPermissionInput(groupId, permissionId);
      var savedAgp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(savedAgp);

      var response = accessGroupPermissionResource.create(input);

      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should handle null access group id in input")
    void shouldHandleNullAccessGroupIdInInput() {
      var input = new AccessGroupPermissionInput(null, permissionId);
      var savedAgp = AccessGroupPermission.builder()
          .accessGroup(null)
          .permission(permission)
          .build();

      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(savedAgp);

      var response = accessGroupPermissionResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
    }

    @Test
    @DisplayName("should handle null permission id in input")
    void shouldHandleNullPermissionIdInInput() {
      var input = new AccessGroupPermissionInput(groupId, null);
      var savedAgp = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(null)
          .build();

      when(accessGroupPermissionService.save(any(AccessGroupPermission.class)))
          .thenReturn(savedAgp);

      var response = accessGroupPermissionResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(accessGroupPermissionService).save(any(AccessGroupPermission.class));
    }
  }
}
