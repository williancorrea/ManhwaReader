package dev.williancorrea.manhwa.reader.features.access.user;

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
@DisplayName("UserService")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();
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

      when(userRepository.findAll()).thenReturn(users);

      var result = userService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(user, user2);
      verify(userRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no users exist")
    void shouldReturnEmptyList() {
      when(userRepository.findAll()).thenReturn(List.of());

      var result = userService.findAll();

      assertThat(result).isEmpty();
      verify(userRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return user when found")
    void shouldReturnUserWhenFound() {
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      var result = userService.findById(userId);

      assertThat(result)
          .isPresent()
          .contains(user);
      verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("should return empty optional when user not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      var result = userService.findById(userId);

      assertThat(result).isEmpty();
      verify(userRepository).findById(userId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return user")
    void shouldSaveAndReturnUser() {
      when(userRepository.save(user)).thenReturn(user);

      var result = userService.save(user);

      assertThat(result)
          .isNotNull()
          .isEqualTo(user);
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should save new user and assign id")
    void shouldSaveNewUserAndAssignId() {
      var newUser = User.builder()
          .name("New User")
          .email("new@example.com")
          .build();
      var savedUser = User.builder()
          .id(UUID.randomUUID())
          .name("New User")
          .email("new@example.com")
          .build();

      when(userRepository.save(newUser)).thenReturn(savedUser);

      var result = userService.save(newUser);

      assertThat(result.getId()).isNotNull();
      verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("should update existing user")
    void shouldUpdateExistingUser() {
      var updatedUser = User.builder()
          .id(userId)
          .name("Updated Name")
          .email("test@example.com")
          .build();

      when(userRepository.save(updatedUser)).thenReturn(updatedUser);

      var result = userService.save(updatedUser);

      assertThat(result.getName()).isEqualTo("Updated Name");
      verify(userRepository).save(updatedUser);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when user exists")
    void shouldReturnTrueWhenUserExists() {
      when(userRepository.existsById(userId)).thenReturn(true);

      var result = userService.existsById(userId);

      assertThat(result).isTrue();
      verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
      when(userRepository.existsById(userId)).thenReturn(false);

      var result = userService.existsById(userId);

      assertThat(result).isFalse();
      verify(userRepository).existsById(userId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete user by id")
    void shouldDeleteUserById() {
      userService.deleteById(userId);

      verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent user")
    void shouldHandleDeletionOfNonExistentUser() {
      var nonExistentId = UUID.randomUUID();

      userService.deleteById(nonExistentId);

      verify(userRepository).deleteById(nonExistentId);
    }
  }
}
