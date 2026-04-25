package dev.williancorrea.manhwa.reader.features.access.user;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroup;
import dev.williancorrea.manhwa.reader.features.access.group.GroupType;
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
@DisplayName("UserAccessGroupService")
class UserAccessGroupServiceTest {

  @Mock
  private UserAccessGroupRepository userAccessGroupRepository;

  @InjectMocks
  private UserAccessGroupService userAccessGroupService;

  private UUID userId;
  private UUID groupId;
  private UserAccessGroupId compositeId;
  private User user;
  private AccessGroup accessGroup;
  private UserAccessGroup userAccessGroup;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    groupId = UUID.randomUUID();
    compositeId = new UserAccessGroupId(userId, groupId);

    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();

    accessGroup = AccessGroup.builder()
        .id(groupId)
        .name(GroupType.READER)
        .build();

    userAccessGroup = UserAccessGroup.builder()
        .user(user)
        .accessGroup(accessGroup)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all user access groups")
    void shouldReturnAllUserAccessGroups() {
      var user2 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var groupId2 = UUID.randomUUID();
      var accessGroup2 = AccessGroup.builder()
          .id(groupId2)
          .name(GroupType.MODERATOR)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user2)
          .accessGroup(accessGroup2)
          .build();
      var userAccessGroups = List.of(userAccessGroup, uag2);

      when(userAccessGroupRepository.findAll()).thenReturn(userAccessGroups);

      var result = userAccessGroupService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(userAccessGroup, uag2);
      verify(userAccessGroupRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no user access groups exist")
    void shouldReturnEmptyList() {
      when(userAccessGroupRepository.findAll()).thenReturn(List.of());

      var result = userAccessGroupService.findAll();

      assertThat(result).isEmpty();
      verify(userAccessGroupRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findAllByUserId()")
  class FindAllByUserIdTests {

    @Test
    @DisplayName("should return all access groups for a user")
    void shouldReturnAllAccessGroupsForUser() {
      var accessGroup2 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup2)
          .build();
      var userAccessGroups = List.of(userAccessGroup, uag2);

      when(userAccessGroupRepository.findAllByUser_Id(userId))
          .thenReturn(userAccessGroups);

      var result = userAccessGroupService.findAllByUserId(userId);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(userAccessGroup, uag2);
      verify(userAccessGroupRepository).findAllByUser_Id(userId);
    }

    @Test
    @DisplayName("should return empty list when user has no access groups")
    void shouldReturnEmptyListWhenUserHasNoAccessGroups() {
      when(userAccessGroupRepository.findAllByUser_Id(userId))
          .thenReturn(List.of());

      var result = userAccessGroupService.findAllByUserId(userId);

      assertThat(result).isEmpty();
      verify(userAccessGroupRepository).findAllByUser_Id(userId);
    }

    @Test
    @DisplayName("should handle multiple access groups for same user")
    void shouldHandleMultipleAccessGroupsForSameUser() {
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var moderatorGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var uploaderGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();

      var uag2 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(adminGroup)
          .build();
      var uag3 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(moderatorGroup)
          .build();
      var uag4 = UserAccessGroup.builder()
          .user(user)
          .accessGroup(uploaderGroup)
          .build();
      var userAccessGroups = List.of(userAccessGroup, uag2, uag3, uag4);

      when(userAccessGroupRepository.findAllByUser_Id(userId))
          .thenReturn(userAccessGroups);

      var result = userAccessGroupService.findAllByUserId(userId);

      assertThat(result)
          .hasSize(4)
          .extracting(uag -> uag.getAccessGroup().getName())
          .contains(
              GroupType.READER,
              GroupType.ADMINISTRATOR,
              GroupType.MODERATOR,
              GroupType.UPLOADER
          );
      verify(userAccessGroupRepository).findAllByUser_Id(userId);
    }
  }

  @Nested
  @DisplayName("findAllByAccessGroupId()")
  class FindAllByAccessGroupIdTests {

    @Test
    @DisplayName("should return all users in an access group")
    void shouldReturnAllUsersInAccessGroup() {
      var user2 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var uag2 = UserAccessGroup.builder()
          .user(user2)
          .accessGroup(accessGroup)
          .build();
      var userAccessGroups = List.of(userAccessGroup, uag2);

      when(userAccessGroupRepository.findAllByAccessGroup_Id(groupId))
          .thenReturn(userAccessGroups);

      var result = userAccessGroupService.findAllByAccessGroupId(groupId);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(userAccessGroup, uag2);
      verify(userAccessGroupRepository).findAllByAccessGroup_Id(groupId);
    }

    @Test
    @DisplayName("should return empty list when access group has no users")
    void shouldReturnEmptyListWhenAccessGroupHasNoUsers() {
      when(userAccessGroupRepository.findAllByAccessGroup_Id(groupId))
          .thenReturn(List.of());

      var result = userAccessGroupService.findAllByAccessGroupId(groupId);

      assertThat(result).isEmpty();
      verify(userAccessGroupRepository).findAllByAccessGroup_Id(groupId);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return user access group when found")
    void shouldReturnUserAccessGroupWhenFound() {
      when(userAccessGroupRepository.findById(compositeId))
          .thenReturn(Optional.of(userAccessGroup));

      var result = userAccessGroupService.findById(compositeId);

      assertThat(result)
          .isPresent()
          .contains(userAccessGroup);
      verify(userAccessGroupRepository).findById(compositeId);
    }

    @Test
    @DisplayName("should return empty optional when user access group not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(userAccessGroupRepository.findById(compositeId))
          .thenReturn(Optional.empty());

      var result = userAccessGroupService.findById(compositeId);

      assertThat(result).isEmpty();
      verify(userAccessGroupRepository).findById(compositeId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return user access group")
    void shouldSaveAndReturnUserAccessGroup() {
      when(userAccessGroupRepository.save(userAccessGroup))
          .thenReturn(userAccessGroup);

      var result = userAccessGroupService.save(userAccessGroup);

      assertThat(result)
          .isNotNull()
          .isEqualTo(userAccessGroup);
      verify(userAccessGroupRepository).save(userAccessGroup);
    }

    @Test
    @DisplayName("should save new user access group")
    void shouldSaveNewUserAccessGroup() {
      var newUser = User.builder()
          .id(UUID.randomUUID())
          .name("New User")
          .email("new@example.com")
          .build();
      var newGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();
      var newUag = UserAccessGroup.builder()
          .user(newUser)
          .accessGroup(newGroup)
          .build();

      when(userAccessGroupRepository.save(newUag))
          .thenReturn(newUag);

      var result = userAccessGroupService.save(newUag);

      assertThat(result).isNotNull();
      verify(userAccessGroupRepository).save(newUag);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when user access group exists")
    void shouldReturnTrueWhenUserAccessGroupExists() {
      when(userAccessGroupRepository.existsById(compositeId))
          .thenReturn(true);

      var result = userAccessGroupService.existsById(compositeId);

      assertThat(result).isTrue();
      verify(userAccessGroupRepository).existsById(compositeId);
    }

    @Test
    @DisplayName("should return false when user access group does not exist")
    void shouldReturnFalseWhenUserAccessGroupDoesNotExist() {
      when(userAccessGroupRepository.existsById(compositeId))
          .thenReturn(false);

      var result = userAccessGroupService.existsById(compositeId);

      assertThat(result).isFalse();
      verify(userAccessGroupRepository).existsById(compositeId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete user access group by id")
    void shouldDeleteUserAccessGroupById() {
      userAccessGroupService.deleteById(compositeId);

      verify(userAccessGroupRepository).deleteById(compositeId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent user access group")
    void shouldHandleDeletionOfNonExistentUserAccessGroup() {
      var nonExistentId = new UserAccessGroupId(UUID.randomUUID(), UUID.randomUUID());

      userAccessGroupService.deleteById(nonExistentId);

      verify(userAccessGroupRepository).deleteById(nonExistentId);
    }
  }
}
