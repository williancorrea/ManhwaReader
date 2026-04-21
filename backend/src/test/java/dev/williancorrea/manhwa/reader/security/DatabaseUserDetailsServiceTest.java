package dev.williancorrea.manhwa.reader.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroup;
import dev.williancorrea.manhwa.reader.features.access.group.AccessGroupPermission;
import dev.williancorrea.manhwa.reader.features.access.group.AccessGroupPermissionRepository;
import dev.williancorrea.manhwa.reader.features.access.group.GroupType;
import dev.williancorrea.manhwa.reader.features.access.permission.Permission;
import dev.williancorrea.manhwa.reader.features.access.permission.PermissionType;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserAccessGroup;
import dev.williancorrea.manhwa.reader.features.access.user.UserAccessGroupRepository;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class DatabaseUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserAccessGroupRepository userAccessGroupRepository;

  @Mock
  private AccessGroupPermissionRepository accessGroupPermissionRepository;

  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private DatabaseUserDetailsService databaseUserDetailsService;

  @SuppressWarnings("resource")
  public DatabaseUserDetailsServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class LoadUserByUsernameTests {

    @Test
    void givenValidUserWithPermissions_whenLoadUserByUsername_thenReturnUserDetails() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();
      UUID groupId = UUID.randomUUID();
      UUID permissionId = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash("hashedPassword123")
          .emailVerified(true)
          .build();

      AccessGroup accessGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.ADMINISTRATOR)
          .build();

      UserAccessGroup userAccessGroup = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      Permission permission = Permission.builder()
          .id(permissionId)
          .name(PermissionType.ADMINISTRATOR)
          .build();

      AccessGroupPermission accessGroupPermission = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(permission)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId)).thenReturn(List.of(userAccessGroup));
      when(accessGroupPermissionRepository.findAllByAccessGroup_IdIn(List.of(groupId)))
          .thenReturn(List.of(accessGroupPermission));

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertEquals("hashedPassword123", result.getPassword());
      assertTrue(result.getAuthorities().stream()
          .anyMatch(auth -> auth.getAuthority().equals(PermissionType.ADMINISTRATOR.name())));
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
      verify(accessGroupPermissionRepository, times(1)).findAllByAccessGroup_IdIn(List.of(groupId));
    }

    @Test
    void givenValidUserWithoutPermissions_whenLoadUserByUsername_thenReturnUserDetailsWithoutAuthorities() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash("hashedPassword123")
          .emailVerified(true)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId)).thenReturn(Collections.emptyList());

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertEquals("hashedPassword123", result.getPassword());
      assertTrue(result.getAuthorities().isEmpty());
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
      verify(accessGroupPermissionRepository, times(0)).findAllByAccessGroup_IdIn(anyList());
    }

    @Test
    void givenUserNotFound_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
      // Arrange
      String email = "nonexistent@example.com";
      String errorMessage = "User not found";

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
      when(messageSource.getMessage("auth.error.user-not-found", null,
          LocaleContextHolder.getLocale())).thenReturn(errorMessage);

      // Act & Assert
      UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
          () -> databaseUserDetailsService.loadUserByUsername(email));

      assertEquals(errorMessage, exception.getMessage());
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(0)).findAllByUser_Id(any());
    }

    @Test
    void givenValidUserWithNullPermission_whenLoadUserByUsername_thenSkipNullPermission() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();
      UUID groupId = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash("hashedPassword123")
          .emailVerified(true)
          .build();

      AccessGroup accessGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.ADMINISTRATOR)
          .build();

      UserAccessGroup userAccessGroup = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      AccessGroupPermission accessGroupPermission = AccessGroupPermission.builder()
          .accessGroup(accessGroup)
          .permission(null)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId)).thenReturn(List.of(userAccessGroup));
      when(accessGroupPermissionRepository.findAllByAccessGroup_IdIn(List.of(groupId)))
          .thenReturn(List.of(accessGroupPermission));

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertTrue(result.getAuthorities().isEmpty());
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
      verify(accessGroupPermissionRepository, times(1)).findAllByAccessGroup_IdIn(List.of(groupId));
    }

    @Test
    void givenValidUserWithNullPasswordHash_whenLoadUserByUsername_thenReturnEmptyPassword() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash(null)
          .emailVerified(true)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId)).thenReturn(Collections.emptyList());

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertEquals("", result.getPassword());
      assertTrue(result.getAuthorities().isEmpty());
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
    }

    @Test
    void givenValidUserWithMultiplePermissions_whenLoadUserByUsername_thenReturnAllPermissions() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();
      UUID groupId1 = UUID.randomUUID();
      UUID groupId2 = UUID.randomUUID();
      UUID permissionId1 = UUID.randomUUID();
      UUID permissionId2 = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash("hashedPassword123")
          .emailVerified(true)
          .build();

      AccessGroup accessGroup1 = AccessGroup.builder()
          .id(groupId1)
          .name(GroupType.ADMINISTRATOR)
          .build();

      AccessGroup accessGroup2 = AccessGroup.builder()
          .id(groupId2)
          .name(GroupType.READER)
          .build();

      UserAccessGroup userAccessGroup1 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup1)
          .build();

      UserAccessGroup userAccessGroup2 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup2)
          .build();

      Permission permission1 = Permission.builder()
          .id(permissionId1)
          .name(PermissionType.ADMINISTRATOR)
          .build();

      Permission permission2 = Permission.builder()
          .id(permissionId2)
          .name(PermissionType.MODERATOR)
          .build();

      AccessGroupPermission accessGroupPermission1 = AccessGroupPermission.builder()
          .accessGroup(accessGroup1)
          .permission(permission1)
          .build();

      AccessGroupPermission accessGroupPermission2 = AccessGroupPermission.builder()
          .accessGroup(accessGroup2)
          .permission(permission2)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId))
          .thenReturn(List.of(userAccessGroup1, userAccessGroup2));
      when(accessGroupPermissionRepository.findAllByAccessGroup_IdIn(List.of(groupId1, groupId2)))
          .thenReturn(List.of(accessGroupPermission1, accessGroupPermission2));

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertEquals("hashedPassword123", result.getPassword());
      assertEquals(2, result.getAuthorities().size());
      assertTrue(result.getAuthorities().stream()
          .anyMatch(auth -> auth.getAuthority().equals(PermissionType.ADMINISTRATOR.name())));
      assertTrue(result.getAuthorities().stream()
          .anyMatch(auth -> auth.getAuthority().equals(PermissionType.MODERATOR.name())));
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
      verify(accessGroupPermissionRepository, times(1))
          .findAllByAccessGroup_IdIn(List.of(groupId1, groupId2));
    }

    @Test
    void givenValidUserWithMultipleGroupsOneWithoutPermissions_whenLoadUserByUsername_thenReturnOnlyValidPermissions() {
      // Arrange
      String email = "test@example.com";
      UUID userId = UUID.randomUUID();
      UUID groupId1 = UUID.randomUUID();
      UUID groupId2 = UUID.randomUUID();
      UUID permissionId = UUID.randomUUID();

      User user = User.builder()
          .id(userId)
          .email(email)
          .name("Test User")
          .passwordHash("hashedPassword123")
          .emailVerified(true)
          .build();

      AccessGroup accessGroup1 = AccessGroup.builder()
          .id(groupId1)
          .name(GroupType.ADMINISTRATOR)
          .build();

      AccessGroup accessGroup2 = AccessGroup.builder()
          .id(groupId2)
          .name(GroupType.READER)
          .build();

      UserAccessGroup userAccessGroup1 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup1)
          .build();

      UserAccessGroup userAccessGroup2 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup2)
          .build();

      Permission permission = Permission.builder()
          .id(permissionId)
          .name(PermissionType.ADMINISTRATOR)
          .build();

      AccessGroupPermission accessGroupPermission = AccessGroupPermission.builder()
          .accessGroup(accessGroup1)
          .permission(permission)
          .build();

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(userAccessGroupRepository.findAllByUser_Id(userId))
          .thenReturn(List.of(userAccessGroup1, userAccessGroup2));
      when(accessGroupPermissionRepository.findAllByAccessGroup_IdIn(List.of(groupId1, groupId2)))
          .thenReturn(List.of(accessGroupPermission));

      // Act
      UserDetails result = databaseUserDetailsService.loadUserByUsername(email);

      // Assert
      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertEquals(1, result.getAuthorities().size());
      assertTrue(result.getAuthorities().stream()
          .anyMatch(auth -> auth.getAuthority().equals(PermissionType.ADMINISTRATOR.name())));
      verify(userRepository, times(1)).findByEmail(email);
      verify(userAccessGroupRepository, times(1)).findAllByUser_Id(userId);
      verify(accessGroupPermissionRepository, times(1))
          .findAllByAccessGroup_IdIn(List.of(groupId1, groupId2));
    }

  }

}
