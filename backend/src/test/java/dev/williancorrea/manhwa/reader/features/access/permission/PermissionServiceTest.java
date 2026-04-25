package dev.williancorrea.manhwa.reader.features.access.permission;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionService")
class PermissionServiceTest {

  @Mock
  private PermissionRepository permissionRepository;

  @InjectMocks
  private PermissionService permissionService;

  private UUID permissionId;
  private Permission permission;

  @BeforeEach
  void setUp() {
    permissionId = UUID.randomUUID();
    permission = Permission.builder()
        .id(permissionId)
        .name(PermissionType.READER)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all permissions")
    void shouldReturnAllPermissions() {
      var permission2 = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.ADMINISTRATOR)
          .build();
      var permissions = List.of(permission, permission2);

      when(permissionRepository.findAll()).thenReturn(permissions);

      var result = permissionService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(permission, permission2);
      verify(permissionRepository).findAll();
    }

    @Test
    @DisplayName("should return all permission types")
    void shouldReturnAllPermissionTypes() {
      var admin = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.ADMINISTRATOR)
          .build();
      var moderator = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.MODERATOR)
          .build();
      var reader = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.READER)
          .build();
      var uploader = Permission.builder()
          .id(UUID.randomUUID())
          .name(PermissionType.UPLOADER)
          .build();
      var permissions = List.of(admin, moderator, reader, uploader);

      when(permissionRepository.findAll()).thenReturn(permissions);

      var result = permissionService.findAll();

      assertThat(result)
          .hasSize(4)
          .extracting(Permission::getName)
          .contains(
              PermissionType.ADMINISTRATOR,
              PermissionType.MODERATOR,
              PermissionType.READER,
              PermissionType.UPLOADER
          );
      verify(permissionRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no permissions exist")
    void shouldReturnEmptyList() {
      when(permissionRepository.findAll()).thenReturn(List.of());

      var result = permissionService.findAll();

      assertThat(result).isEmpty();
      verify(permissionRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return permission when found")
    void shouldReturnPermissionWhenFound() {
      when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));

      var result = permissionService.findById(permissionId);

      assertThat(result)
          .isPresent()
          .contains(permission);
      verify(permissionRepository).findById(permissionId);
    }

    @Test
    @DisplayName("should return empty optional when permission not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

      var result = permissionService.findById(permissionId);

      assertThat(result).isEmpty();
      verify(permissionRepository).findById(permissionId);
    }

    @Test
    @DisplayName("should return correct permission by id")
    void shouldReturnCorrectPermissionById() {
      var adminPermission = Permission.builder()
          .id(permissionId)
          .name(PermissionType.ADMINISTRATOR)
          .build();

      when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(adminPermission));

      var result = permissionService.findById(permissionId);

      assertThat(result)
          .isPresent()
          .get()
          .satisfies(p -> {
            assertThat(p.getId()).isEqualTo(permissionId);
            assertThat(p.getName()).isEqualTo(PermissionType.ADMINISTRATOR);
          });
      verify(permissionRepository).findById(permissionId);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when permission exists")
    void shouldReturnTrueWhenPermissionExists() {
      when(permissionRepository.existsById(permissionId)).thenReturn(true);

      var result = permissionService.existsById(permissionId);

      assertThat(result).isTrue();
      verify(permissionRepository).existsById(permissionId);
    }

    @Test
    @DisplayName("should return false when permission does not exist")
    void shouldReturnFalseWhenPermissionDoesNotExist() {
      when(permissionRepository.existsById(permissionId)).thenReturn(false);

      var result = permissionService.existsById(permissionId);

      assertThat(result).isFalse();
      verify(permissionRepository).existsById(permissionId);
    }

    @Test
    @DisplayName("should check existence for each permission type")
    void shouldCheckExistenceForEachPermissionType() {
      var adminId = UUID.randomUUID();
      var moderatorId = UUID.randomUUID();
      var readerId = UUID.randomUUID();
      var uploaderId = UUID.randomUUID();

      when(permissionRepository.existsById(adminId)).thenReturn(true);
      when(permissionRepository.existsById(moderatorId)).thenReturn(true);
      when(permissionRepository.existsById(readerId)).thenReturn(true);
      when(permissionRepository.existsById(uploaderId)).thenReturn(true);

      assertThat(permissionService.existsById(adminId)).isTrue();
      assertThat(permissionService.existsById(moderatorId)).isTrue();
      assertThat(permissionService.existsById(readerId)).isTrue();
      assertThat(permissionService.existsById(uploaderId)).isTrue();

      verify(permissionRepository).existsById(adminId);
      verify(permissionRepository).existsById(moderatorId);
      verify(permissionRepository).existsById(readerId);
      verify(permissionRepository).existsById(uploaderId);
    }
  }
}
