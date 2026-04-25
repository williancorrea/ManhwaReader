package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupInput")
class AccessGroupInputTest {

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create input with name")
    void shouldCreateInputWithName() {
      var input = new AccessGroupInput("READER");

      assertThat(input.getName()).isEqualTo("READER");
    }

    @Test
    @DisplayName("should create input with all group type names")
    void shouldCreateInputWithAllGroupTypeNames() {
      for (var type : GroupType.values()) {
        var input = new AccessGroupInput(type.name());

        assertThat(input.getName()).isEqualTo(type.name());
      }
    }
  }

  @Nested
  @DisplayName("NoArgsConstructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty input")
    void shouldCreateEmptyInput() {
      var input = new AccessGroupInput();

      assertThat(input.getName()).isNull();
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get name")
    void shouldSetAndGetName() {
      var input = new AccessGroupInput();
      input.setName("ADMINISTRATOR");

      assertThat(input.getName()).isEqualTo("ADMINISTRATOR");
    }

    @Test
    @DisplayName("should update name")
    void shouldUpdateName() {
      var input = new AccessGroupInput("READER");
      input.setName("MODERATOR");

      assertThat(input.getName()).isEqualTo("MODERATOR");
    }

    @Test
    @DisplayName("should set name to null")
    void shouldSetNameToNull() {
      var input = new AccessGroupInput("UPLOADER");
      input.setName(null);

      assertThat(input.getName()).isNull();
    }
  }

  @Nested
  @DisplayName("Name Validation")
  class NameValidationTests {

    @Test
    @DisplayName("should accept valid group type names")
    void shouldAcceptValidGroupTypeNames() {
      var names = new String[]{"ADMINISTRATOR", "MODERATOR", "READER", "UPLOADER"};

      for (var name : names) {
        var input = new AccessGroupInput(name);

        assertThat(input.getName()).isEqualTo(name);
      }
    }

    @Test
    @DisplayName("should accept lowercase names")
    void shouldAcceptLowercaseNames() {
      var input = new AccessGroupInput("reader");

      assertThat(input.getName()).isEqualTo("reader");
    }

    @Test
    @DisplayName("should accept mixed case names")
    void shouldAcceptMixedCaseNames() {
      var input = new AccessGroupInput("ReAdEr");

      assertThat(input.getName()).isEqualTo("ReAdEr");
    }
  }

  @Nested
  @DisplayName("Boundary Cases")
  class BoundaryCasesTests {

    @Test
    @DisplayName("should accept max length name")
    void shouldAcceptMaxLengthName() {
      var maxName = "A".repeat(100);
      var input = new AccessGroupInput(maxName);

      assertThat(input.getName()).isEqualTo(maxName);
      assertThat(input.getName()).hasSize(100);
    }

    @Test
    @DisplayName("should accept single character name")
    void shouldAcceptSingleCharacterName() {
      var input = new AccessGroupInput("A");

      assertThat(input.getName()).isEqualTo("A");
    }

    @Test
    @DisplayName("should accept empty string")
    void shouldAcceptEmptyString() {
      var input = new AccessGroupInput("");

      assertThat(input.getName()).isEmpty();
    }
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create input with all args")
    void shouldCreateInputWithAllArgs() {
      var input = new AccessGroupInput("UPLOADER");

      assertThat(input.getName()).isEqualTo("UPLOADER");
    }
  }

  @Nested
  @DisplayName("Multiple Instances")
  class MultipleInstancesTests {

    @Test
    @DisplayName("should create multiple independent instances")
    void shouldCreateMultipleIndependentInstances() {
      var input1 = new AccessGroupInput("READER");
      var input2 = new AccessGroupInput("MODERATOR");
      var input3 = new AccessGroupInput("ADMINISTRATOR");

      assertThat(input1.getName()).isEqualTo("READER");
      assertThat(input2.getName()).isEqualTo("MODERATOR");
      assertThat(input3.getName()).isEqualTo("ADMINISTRATOR");
    }

    @Test
    @DisplayName("should modify instances independently")
    void shouldModifyInstancesIndependently() {
      var input1 = new AccessGroupInput("READER");
      var input2 = new AccessGroupInput("READER");

      input1.setName("MODERATOR");

      assertThat(input1.getName()).isEqualTo("MODERATOR");
      assertThat(input2.getName()).isEqualTo("READER");
    }
  }
}
