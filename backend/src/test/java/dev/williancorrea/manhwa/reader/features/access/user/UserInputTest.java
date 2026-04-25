package dev.williancorrea.manhwa.reader.features.access.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserInput")
class UserInputTest {

  private String userName;
  private String userEmail;
  private String passwordHash;

  @BeforeEach
  void setUp() {
    userName = "Test User";
    userEmail = "test@example.com";
    passwordHash = "hashedPassword123";
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create user input with all fields")
    void shouldCreateUserInputWithAllFields() {
      var input = new UserInput(userName, userEmail, passwordHash);

      assertThat(input.getName()).isEqualTo(userName);
      assertThat(input.getEmail()).isEqualTo(userEmail);
      assertThat(input.getPasswordHash()).isEqualTo(passwordHash);
    }

    @Test
    @DisplayName("should create empty user input with no-args constructor")
    void shouldCreateEmptyUserInputWithNoArgsConstructor() {
      var input = new UserInput();

      assertThat(input.getName()).isNull();
      assertThat(input.getEmail()).isNull();
      assertThat(input.getPasswordHash()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get name")
    void shouldSetAndGetName() {
      var input = new UserInput();
      input.setName(userName);

      assertThat(input.getName()).isEqualTo(userName);
    }

    @Test
    @DisplayName("should set and get email")
    void shouldSetAndGetEmail() {
      var input = new UserInput();
      input.setEmail(userEmail);

      assertThat(input.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("should set and get password hash")
    void shouldSetAndGetPasswordHash() {
      var input = new UserInput();
      input.setPasswordHash(passwordHash);

      assertThat(input.getPasswordHash()).isEqualTo(passwordHash);
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetAllFields() {
      var input = new UserInput();
      input.setName(userName);
      input.setEmail(userEmail);
      input.setPasswordHash(passwordHash);

      assertThat(input.getName()).isEqualTo(userName);
      assertThat(input.getEmail()).isEqualTo(userEmail);
      assertThat(input.getPasswordHash()).isEqualTo(passwordHash);
    }
  }

  @Nested
  @DisplayName("All-args constructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create user input with name and email")
    void shouldCreateUserInputWithNameAndEmail() {
      var input = new UserInput("John Doe", "john@example.com", "hash123");

      assertThat(input.getName()).isEqualTo("John Doe");
      assertThat(input.getEmail()).isEqualTo("john@example.com");
      assertThat(input.getPasswordHash()).isEqualTo("hash123");
    }

    @Test
    @DisplayName("should create user input with different credentials")
    void shouldCreateUserInputWithDifferentCredentials() {
      var name = "Jane Smith";
      var email = "jane@example.com";
      var hash = "anotherHash456";

      var input = new UserInput(name, email, hash);

      assertThat(input.getName()).isEqualTo(name);
      assertThat(input.getEmail()).isEqualTo(email);
      assertThat(input.getPasswordHash()).isEqualTo(hash);
    }
  }

  @Nested
  @DisplayName("Field validation")
  class FieldValidationTests {

    @Test
    @DisplayName("should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
      var validEmails = new String[]{
          "test@example.com",
          "user.name@example.co.uk",
          "first+last@example.org"
      };

      for (var email : validEmails) {
        var input = new UserInput(userName, email, passwordHash);
        assertThat(input.getEmail()).isEqualTo(email);
      }
    }

    @Test
    @DisplayName("should support empty password hash")
    void shouldSupportEmptyPasswordHash() {
      var input = new UserInput(userName, userEmail, "");

      assertThat(input.getPasswordHash()).isEmpty();
    }

    @Test
    @DisplayName("should support null password hash")
    void shouldSupportNullPasswordHash() {
      var input = new UserInput(userName, userEmail, null);

      assertThat(input.getPasswordHash()).isNull();
    }

    @Test
    @DisplayName("should support different name lengths")
    void shouldSupportDifferentNameLengths() {
      var shortName = "Jo";
      var longName = "A".repeat(100);

      var input1 = new UserInput(shortName, userEmail, passwordHash);
      var input2 = new UserInput(longName, userEmail, passwordHash);

      assertThat(input1.getName()).isEqualTo(shortName);
      assertThat(input2.getName()).isEqualTo(longName);
    }
  }
}
