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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAccessGroupResource")
class UserAccessGroupResourceTest {

  @Mock
  private UserAccessGroupService userAccessGroupService;

  @InjectMocks
  private UserAccessGroupResource userAccessGroupResource;

  private UUID userId;
  private UUID groupId;
  private UserAccessGroupId compositeId;
  private User user;
  private AccessGroup accessGroup;
  private UserAccessGroup userAccessGroup;
  private UserAccessGroupInput input;

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

    input = new UserAccessGroupInput(userId, groupId);
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

      when(userAccessGroupService.findAll()).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(userAccessGroupService).findAll();
    }

    @Test
    @DisplayName("should return empty list when no user access groups exist")
    void shouldReturnEmptyListWhenNoUserAccessGroupsExist() {
      when(userAccessGroupService.findAll()).thenReturn(List.of());

      var response = userAccessGroupResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(userAccessGroupService).findAll();
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var userAccessGroups = List.of(userAccessGroup);

      when(userAccessGroupService.findAll()).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAll();

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> {
            assertThat(body.getFirst().getUserId()).isEqualTo(userId);
            assertThat(body.getFirst().getAccessGroupId()).isEqualTo(groupId);
          });
      verify(userAccessGroupService).findAll();
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create and return user access group")
    void shouldCreateAndReturnUserAccessGroup() {
      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(userAccessGroup);

      var response = userAccessGroupResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(userId);
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
          });
      verify(userAccessGroupService).save(any(UserAccessGroup.class));
    }

    @Test
    @DisplayName("should convert input to entity")
    void shouldConvertInputToEntity() {
      var newUserId = UUID.randomUUID();
      var newGroupId = UUID.randomUUID();
      var inputData = new UserAccessGroupInput(newUserId, newGroupId);
      var newUser = User.builder().id(newUserId).name("New User").email("new@example.com").build();
      var newGroup = AccessGroup.builder().id(newGroupId).name(GroupType.ADMINISTRATOR).build();
      var savedUag = UserAccessGroup.builder().user(newUser).accessGroup(newGroup).build();

      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(savedUag);

      var response = userAccessGroupResource.create(inputData);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(newUserId);
            assertThat(body.getAccessGroupId()).isEqualTo(newGroupId);
          });
      verify(userAccessGroupService).save(any(UserAccessGroup.class));
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return user access group when found")
    void shouldReturnUserAccessGroupWhenFound() {
      when(userAccessGroupService.findById(compositeId)).thenReturn(Optional.of(userAccessGroup));

      var response = userAccessGroupResource.findById(userId, groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(userId);
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
          });
      verify(userAccessGroupService).findById(compositeId);
    }

    @Test
    @DisplayName("should return not found when user access group does not exist")
    void shouldReturnNotFoundWhenUserAccessGroupDoesNotExist() {
      when(userAccessGroupService.findById(compositeId)).thenReturn(Optional.empty());

      var response = userAccessGroupResource.findById(userId, groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userAccessGroupService).findById(compositeId);
    }

    @Test
    @DisplayName("should convert entity to output DTO")
    void shouldConvertEntityToOutputDTO() {
      when(userAccessGroupService.findById(compositeId)).thenReturn(Optional.of(userAccessGroup));

      var response = userAccessGroupResource.findById(userId, groupId);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(userAccessGroup.getUser().getId());
            assertThat(body.getAccessGroupId()).isEqualTo(userAccessGroup.getAccessGroup().getId());
          });
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update and return user access group")
    void shouldUpdateAndReturnUserAccessGroup() {
      var updatedUag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      when(userAccessGroupService.existsById(compositeId)).thenReturn(true);
      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(updatedUag);

      var response = userAccessGroupResource.update(userId, groupId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(userId);
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
          });
      verify(userAccessGroupService).existsById(compositeId);
      verify(userAccessGroupService).save(any(UserAccessGroup.class));
    }

    @Test
    @DisplayName("should return not found when user access group does not exist")
    void shouldReturnNotFoundWhenUserAccessGroupDoesNotExist() {
      when(userAccessGroupService.existsById(compositeId)).thenReturn(false);

      var response = userAccessGroupResource.update(userId, groupId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userAccessGroupService).existsById(compositeId);
    }

    @Test
    @DisplayName("should preserve composite id during update")
    void shouldPreserveCompositeIdDuringUpdate() {
      var updatedUag = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      when(userAccessGroupService.existsById(compositeId)).thenReturn(true);
      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(updatedUag);

      var response = userAccessGroupResource.update(userId, groupId, input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getUserId()).isEqualTo(userId);
            assertThat(body.getAccessGroupId()).isEqualTo(groupId);
          });
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete user access group successfully")
    void shouldDeleteUserAccessGroupSuccessfully() {
      when(userAccessGroupService.existsById(compositeId)).thenReturn(true);

      var response = userAccessGroupResource.delete(userId, groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(userAccessGroupService).existsById(compositeId);
      verify(userAccessGroupService).deleteById(compositeId);
    }

    @Test
    @DisplayName("should return not found when user access group does not exist")
    void shouldReturnNotFoundWhenUserAccessGroupDoesNotExist() {
      when(userAccessGroupService.existsById(compositeId)).thenReturn(false);

      var response = userAccessGroupResource.delete(userId, groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userAccessGroupService).existsById(compositeId);
    }

    @Test
    @DisplayName("should not call deleteById if user access group does not exist")
    void shouldNotCallDeleteByIdIfUserAccessGroupDoesNotExist() {
      when(userAccessGroupService.existsById(compositeId)).thenReturn(false);

      userAccessGroupResource.delete(userId, groupId);

      verify(userAccessGroupService).existsById(compositeId);
    }
  }

  @Nested
  @DisplayName("findAllByUser()")
  class FindAllByUserTests {

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

      when(userAccessGroupService.findAllByUserId(userId)).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAllByUser(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(userAccessGroupService).findAllByUserId(userId);
    }

    @Test
    @DisplayName("should return empty list when user has no access groups")
    void shouldReturnEmptyListWhenUserHasNoAccessGroups() {
      when(userAccessGroupService.findAllByUserId(userId)).thenReturn(List.of());

      var response = userAccessGroupResource.findAllByUser(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(userAccessGroupService).findAllByUserId(userId);
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var userAccessGroups = List.of(userAccessGroup);

      when(userAccessGroupService.findAllByUserId(userId)).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAllByUser(userId);

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> assertThat(body.getFirst().getUserId()).isEqualTo(userId));
    }
  }

  @Nested
  @DisplayName("findAllByAccessGroup()")
  class FindAllByAccessGroupTests {

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

      when(userAccessGroupService.findAllByAccessGroupId(groupId)).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAllByAccessGroup(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(userAccessGroupService).findAllByAccessGroupId(groupId);
    }

    @Test
    @DisplayName("should return empty list when access group has no users")
    void shouldReturnEmptyListWhenAccessGroupHasNoUsers() {
      when(userAccessGroupService.findAllByAccessGroupId(groupId)).thenReturn(List.of());

      var response = userAccessGroupResource.findAllByAccessGroup(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(userAccessGroupService).findAllByAccessGroupId(groupId);
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var userAccessGroups = List.of(userAccessGroup);

      when(userAccessGroupService.findAllByAccessGroupId(groupId)).thenReturn(userAccessGroups);

      var response = userAccessGroupResource.findAllByAccessGroup(groupId);

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> assertThat(body.getFirst().getAccessGroupId()).isEqualTo(groupId));
    }
  }

  @Nested
  @DisplayName("toEntity()")
  class ToEntityTests {

    @Test
    @DisplayName("should convert input to entity correctly")
    void shouldConvertInputToEntityCorrectly() {
      var inputData = new UserAccessGroupInput(userId, groupId);
      var savedEntity = UserAccessGroup.builder()
          .user(user)
          .accessGroup(accessGroup)
          .build();

      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(savedEntity);

      var response = userAccessGroupResource.create(inputData);

      verify(userAccessGroupService).save(any(UserAccessGroup.class));
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should handle null user id in input")
    void shouldHandleNullUserIdInInput() {
      var inputData = new UserAccessGroupInput(null, groupId);
      var savedEntity = UserAccessGroup.builder()
          .user(null)
          .accessGroup(accessGroup)
          .build();

      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(savedEntity);

      var response = userAccessGroupResource.create(inputData);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(userAccessGroupService).save(any(UserAccessGroup.class));
    }

    @Test
    @DisplayName("should handle null access group id in input")
    void shouldHandleNullAccessGroupIdInInput() {
      var inputData = new UserAccessGroupInput(userId, null);
      var savedEntity = UserAccessGroup.builder()
          .user(user)
          .accessGroup(null)
          .build();

      when(userAccessGroupService.save(any(UserAccessGroup.class))).thenReturn(savedEntity);

      var response = userAccessGroupResource.create(inputData);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(userAccessGroupService).save(any(UserAccessGroup.class));
    }
  }
}
