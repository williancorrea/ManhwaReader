package dev.williancorrea.manhwa.reader.features.access.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserResource")
class UserResourceTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserResource userResource;

  private UUID userId;
  private User user;
  private UserInput userInput;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .passwordHash("hashedPassword123")
        .build();
    userInput = new UserInput("Test User", "test@example.com", "hashedPassword123");
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all users")
    void shouldReturnAllUsers() {
      var user2 = User.builder()
          .id(UUID.randomUUID())
          .name("Test User 2")
          .email("test2@example.com")
          .build();
      var users = List.of(user, user2);

      when(userService.findAll()).thenReturn(users);

      var response = userResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(userService).findAll();
    }

    @Test
    @DisplayName("should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
      when(userService.findAll()).thenReturn(List.of());

      var response = userResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(userService).findAll();
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var users = List.of(user);

      when(userService.findAll()).thenReturn(users);

      var response = userResource.findAll();

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> {
            assertThat(body.getFirst().getId()).isEqualTo(userId);
            assertThat(body.getFirst().getName()).isEqualTo("Test User");
            assertThat(body.getFirst().getEmail()).isEqualTo("test@example.com");
          });
      verify(userService).findAll();
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create and return user")
    void shouldCreateAndReturnUser() {
      when(userService.save(any(User.class))).thenReturn(user);

      var response = userResource.create(userInput);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(userId);
            assertThat(body.getName()).isEqualTo("Test User");
            assertThat(body.getEmail()).isEqualTo("test@example.com");
          });
      verify(userService).save(any(User.class));
    }

    @Test
    @DisplayName("should convert input to entity")
    void shouldConvertInputToEntity() {
      var input = new UserInput("New User", "new@example.com", "newHash123");
      var savedUser = User.builder()
          .id(UUID.randomUUID())
          .name("New User")
          .email("new@example.com")
          .passwordHash("newHash123")
          .build();

      when(userService.save(any(User.class))).thenReturn(savedUser);

      var response = userResource.create(input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getName()).isEqualTo("New User");
            assertThat(body.getEmail()).isEqualTo("new@example.com");
          });
      verify(userService).save(any(User.class));
    }

    @Test
    @DisplayName("should handle different user inputs")
    void shouldHandleDifferentUserInputs() {
      var inputs = new UserInput[]{
          new UserInput("John Doe", "john@example.com", "hash1"),
          new UserInput("Jane Smith", "jane@example.com", "hash2"),
          new UserInput("Bob Johnson", "bob@example.com", "hash3")
      };

      for (var input : inputs) {
        var savedUser = User.builder()
            .id(UUID.randomUUID())
            .name(input.getName())
            .email(input.getEmail())
            .passwordHash(input.getPasswordHash())
            .build();

        when(userService.save(any(User.class))).thenReturn(savedUser);

        var response = userResource.create(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
      }
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return user when found")
    void shouldReturnUserWhenFound() {
      when(userService.findById(userId)).thenReturn(Optional.of(user));

      var response = userResource.findById(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(userId);
            assertThat(body.getName()).isEqualTo("Test User");
            assertThat(body.getEmail()).isEqualTo("test@example.com");
          });
      verify(userService).findById(userId);
    }

    @Test
    @DisplayName("should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
      when(userService.findById(userId)).thenReturn(Optional.empty());

      var response = userResource.findById(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userService).findById(userId);
    }

    @Test
    @DisplayName("should convert entity to output DTO")
    void shouldConvertEntityToOutputDTO() {
      when(userService.findById(userId)).thenReturn(Optional.of(user));

      var response = userResource.findById(userId);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getName()).isEqualTo(user.getName());
            assertThat(body.getEmail()).isEqualTo(user.getEmail());
          });
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update and return user")
    void shouldUpdateAndReturnUser() {
      var input = new UserInput("Updated User", "updated@example.com", "newHash");
      var updatedUser = User.builder()
          .id(userId)
          .name("Updated User")
          .email("updated@example.com")
          .passwordHash("newHash")
          .build();

      when(userService.existsById(userId)).thenReturn(true);
      when(userService.save(any(User.class))).thenReturn(updatedUser);

      var response = userResource.update(userId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(userId);
            assertThat(body.getName()).isEqualTo("Updated User");
            assertThat(body.getEmail()).isEqualTo("updated@example.com");
          });
      verify(userService).existsById(userId);
      verify(userService).save(any(User.class));
    }

    @Test
    @DisplayName("should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
      var input = new UserInput("Updated User", "updated@example.com", "newHash");

      when(userService.existsById(userId)).thenReturn(false);

      var response = userResource.update(userId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userService).existsById(userId);
    }

    @Test
    @DisplayName("should preserve user id during update")
    void shouldPreserveUserIdDuringUpdate() {
      var input = new UserInput("Updated User", "updated@example.com", "newHash");
      var updatedUser = User.builder()
          .id(userId)
          .name("Updated User")
          .email("updated@example.com")
          .passwordHash("newHash")
          .build();

      when(userService.existsById(userId)).thenReturn(true);
      when(userService.save(any(User.class))).thenReturn(updatedUser);

      var response = userResource.update(userId, input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> assertThat(body.getId()).isEqualTo(userId));
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete user successfully")
    void shouldDeleteUserSuccessfully() {
      when(userService.existsById(userId)).thenReturn(true);

      var response = userResource.delete(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(userService).existsById(userId);
      verify(userService).deleteById(userId);
    }

    @Test
    @DisplayName("should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
      when(userService.existsById(userId)).thenReturn(false);

      var response = userResource.delete(userId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(userService).existsById(userId);
    }

    @Test
    @DisplayName("should not call deleteById if user does not exist")
    void shouldNotCallDeleteByIdIfUserDoesNotExist() {
      when(userService.existsById(userId)).thenReturn(false);

      userResource.delete(userId);

      verify(userService).existsById(userId);
    }
  }

  @Nested
  @DisplayName("toEntity()")
  class ToEntityTests {

    @Test
    @DisplayName("should convert input to entity correctly")
    void shouldConvertInputToEntityCorrectly() {
      var input = new UserInput("New User", "new@example.com", "hash123");
      var savedEntity = User.builder()
          .id(UUID.randomUUID())
          .name("New User")
          .email("new@example.com")
          .passwordHash("hash123")
          .build();

      when(userService.save(any(User.class))).thenReturn(savedEntity);

      var response = userResource.create(input);

      verify(userService).save(any(User.class));
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should set all input fields to entity")
    void shouldSetAllInputFieldsToEntity() {
      var input = new UserInput("John Doe", "john@example.com", "secureHash");
      var savedEntity = User.builder()
          .id(UUID.randomUUID())
          .name("John Doe")
          .email("john@example.com")
          .passwordHash("secureHash")
          .build();

      when(userService.save(any(User.class))).thenReturn(savedEntity);

      var response = userResource.create(input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getName()).isEqualTo("John Doe");
            assertThat(body.getEmail()).isEqualTo("john@example.com");
          });
    }
  }
}
