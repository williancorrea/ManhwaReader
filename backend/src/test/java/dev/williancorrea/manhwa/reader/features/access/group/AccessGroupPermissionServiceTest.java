package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessGroupPermissionService")
class AccessGroupPermissionServiceTest {

  @Mock
  private AccessGroupPermissionRepository accessGroupPermissionRepository;

  @InjectMocks
  private AccessGroupPermissionService accessGroupPermissionService;

  private UUID groupId;
  private UUID permissionId;
  private AccessGroupPermissionId compositeId;
  private AccessGroupPermission accessGroupPermission;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    permissionId = UUID.randomUUID();
    compositeId = new AccessGroupPermissionId(groupId, permissionId);
    accessGroupPermission = new AccessGroupPermission();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all access group permissions")
    void shouldReturnAllAccessGroupPermissions() {
      var agp2 = new AccessGroupPermission();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionRepository.findAll()).thenReturn(permissions);

      var result = accessGroupPermissionService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(accessGroupPermission, agp2);
      verify(accessGroupPermissionRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no access group permissions exist")
    void shouldReturnEmptyList() {
      when(accessGroupPermissionRepository.findAll()).thenReturn(List.of());

      var result = accessGroupPermissionService.findAll();

      assertThat(result).isEmpty();
      verify(accessGroupPermissionRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findAllByAccessGroupId()")
  class FindAllByAccessGroupIdTests {

    @Test
    @DisplayName("should return all permissions for an access group")
    void shouldReturnAllPermissionsForAccessGroup() {
      var agp2 = new AccessGroupPermission();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionRepository.findAllByAccessGroup_Id(groupId))
          .thenReturn(permissions);

      var result = accessGroupPermissionService.findAllByAccessGroupId(groupId);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(accessGroupPermission, agp2);
      verify(accessGroupPermissionRepository).findAllByAccessGroup_Id(groupId);
    }

    @Test
    @DisplayName("should return empty list when access group has no permissions")
    void shouldReturnEmptyListWhenNoPermissions() {
      when(accessGroupPermissionRepository.findAllByAccessGroup_Id(groupId))
          .thenReturn(List.of());

      var result = accessGroupPermissionService.findAllByAccessGroupId(groupId);

      assertThat(result).isEmpty();
      verify(accessGroupPermissionRepository).findAllByAccessGroup_Id(groupId);
    }
  }

  @Nested
  @DisplayName("findAllByPermissionId()")
  class FindAllByPermissionIdTests {

    @Test
    @DisplayName("should return all access groups for a permission")
    void shouldReturnAllAccessGroupsForPermission() {
      var agp2 = new AccessGroupPermission();
      var permissions = List.of(accessGroupPermission, agp2);

      when(accessGroupPermissionRepository.findAllByPermission_Id(permissionId))
          .thenReturn(permissions);

      var result = accessGroupPermissionService.findAllByPermissionId(permissionId);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(accessGroupPermission, agp2);
      verify(accessGroupPermissionRepository).findAllByPermission_Id(permissionId);
    }

    @Test
    @DisplayName("should return empty list when permission has no access groups")
    void shouldReturnEmptyListWhenNoAccessGroups() {
      when(accessGroupPermissionRepository.findAllByPermission_Id(permissionId))
          .thenReturn(List.of());

      var result = accessGroupPermissionService.findAllByPermissionId(permissionId);

      assertThat(result).isEmpty();
      verify(accessGroupPermissionRepository).findAllByPermission_Id(permissionId);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return access group permission when found")
    void shouldReturnAccessGroupPermissionWhenFound() {
      when(accessGroupPermissionRepository.findById(compositeId))
          .thenReturn(Optional.of(accessGroupPermission));

      var result = accessGroupPermissionService.findById(compositeId);

      assertThat(result)
          .isPresent()
          .contains(accessGroupPermission);
      verify(accessGroupPermissionRepository).findById(compositeId);
    }

    @Test
    @DisplayName("should return empty optional when access group permission not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(accessGroupPermissionRepository.findById(compositeId))
          .thenReturn(Optional.empty());

      var result = accessGroupPermissionService.findById(compositeId);

      assertThat(result).isEmpty();
      verify(accessGroupPermissionRepository).findById(compositeId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return access group permission")
    void shouldSaveAndReturnAccessGroupPermission() {
      when(accessGroupPermissionRepository.save(accessGroupPermission))
          .thenReturn(accessGroupPermission);

      var result = accessGroupPermissionService.save(accessGroupPermission);

      assertThat(result)
          .isNotNull()
          .isEqualTo(accessGroupPermission);
      verify(accessGroupPermissionRepository).save(accessGroupPermission);
    }

    @Test
    @DisplayName("should save new access group permission")
    void shouldSaveNewAccessGroupPermission() {
      var newAgp = new AccessGroupPermission();

      when(accessGroupPermissionRepository.save(newAgp))
          .thenReturn(newAgp);

      var result = accessGroupPermissionService.save(newAgp);

      assertThat(result).isNotNull();
      verify(accessGroupPermissionRepository).save(newAgp);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when access group permission exists")
    void shouldReturnTrueWhenAccessGroupPermissionExists() {
      when(accessGroupPermissionRepository.existsById(compositeId))
          .thenReturn(true);

      var result = accessGroupPermissionService.existsById(compositeId);

      assertThat(result).isTrue();
      verify(accessGroupPermissionRepository).existsById(compositeId);
    }

    @Test
    @DisplayName("should return false when access group permission does not exist")
    void shouldReturnFalseWhenAccessGroupPermissionDoesNotExist() {
      when(accessGroupPermissionRepository.existsById(compositeId))
          .thenReturn(false);

      var result = accessGroupPermissionService.existsById(compositeId);

      assertThat(result).isFalse();
      verify(accessGroupPermissionRepository).existsById(compositeId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete access group permission by id")
    void shouldDeleteAccessGroupPermissionById() {
      accessGroupPermissionService.deleteById(compositeId);

      verify(accessGroupPermissionRepository).deleteById(compositeId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent access group permission")
    void shouldHandleDeletionOfNonExistentAccessGroupPermission() {
      var nonExistentId = new AccessGroupPermissionId(UUID.randomUUID(), UUID.randomUUID());

      accessGroupPermissionService.deleteById(nonExistentId);

      verify(accessGroupPermissionRepository).deleteById(nonExistentId);
    }
  }
}
