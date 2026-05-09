package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LinkWorkInput")
class LinkWorkInputTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Nested
  @DisplayName("record structure")
  class RecordStructureTests {

    @Test
    @DisplayName("should create record with valid values")
    void shouldCreateRecordWithValidValues() {
      var workId = UUID.randomUUID();
      var mangaDexId = "manga-123";

      var input = new LinkWorkInput(workId, mangaDexId);

      assertThat(input.workId()).isEqualTo(workId);
      assertThat(input.mangaDexId()).isEqualTo(mangaDexId);
    }

    @Test
    @DisplayName("should have equals() method")
    void shouldHaveEqualsMethod() {
      var workId = UUID.randomUUID();
      var mangaDexId = "manga-123";

      var input1 = new LinkWorkInput(workId, mangaDexId);
      var input2 = new LinkWorkInput(workId, mangaDexId);

      assertThat(input1).isEqualTo(input2);
    }

    @Test
    @DisplayName("should have hashCode() method")
    void shouldHaveHashCodeMethod() {
      var workId = UUID.randomUUID();
      var mangaDexId = "manga-123";

      var input1 = new LinkWorkInput(workId, mangaDexId);
      var input2 = new LinkWorkInput(workId, mangaDexId);

      assertThat(input1.hashCode()).isEqualTo(input2.hashCode());
    }

    @Test
    @DisplayName("should have toString() method")
    void shouldHaveToStringMethod() {
      var workId = UUID.randomUUID();
      var mangaDexId = "manga-123";

      var input = new LinkWorkInput(workId, mangaDexId);
      var toString = input.toString();

      assertThat(toString).contains("workId", "mangaDexId");
    }
  }

  @Nested
  @DisplayName("validation")
  class ValidationTests {

    @Test
    @DisplayName("should validate with valid workId and mangaDexId")
    void shouldValidateWithValidInputs() {
      var input = new LinkWorkInput(UUID.randomUUID(), "manga-123");

      Set<ConstraintViolation<LinkWorkInput>> violations = validator.validate(input);

      assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("should fail validation when workId is null")
    void shouldFailValidationWhenWorkIdIsNull() {
      var input = new LinkWorkInput(null, "manga-123");

      Set<ConstraintViolation<LinkWorkInput>> violations = validator.validate(input);

      assertThat(violations)
          .isNotEmpty()
          .hasSize(1);
      assertThat(violations.iterator().next().getPropertyPath().toString())
          .isEqualTo("workId");
    }

    @Test
    @DisplayName("should fail validation when mangaDexId is null")
    void shouldFailValidationWhenMangaDexIdIsNull() {
      var input = new LinkWorkInput(UUID.randomUUID(), null);

      Set<ConstraintViolation<LinkWorkInput>> violations = validator.validate(input);

      assertThat(violations)
          .isNotEmpty()
          .hasSize(1);
      assertThat(violations.iterator().next().getPropertyPath().toString())
          .isEqualTo("mangaDexId");
    }

    @Test
    @DisplayName("should fail validation when mangaDexId is blank")
    void shouldFailValidationWhenMangaDexIdIsBlank() {
      var input = new LinkWorkInput(UUID.randomUUID(), "   ");

      Set<ConstraintViolation<LinkWorkInput>> violations = validator.validate(input);

      assertThat(violations)
          .isNotEmpty()
          .hasSize(1);
      assertThat(violations.iterator().next().getPropertyPath().toString())
          .isEqualTo("mangaDexId");
    }

    @Test
    @DisplayName("should fail validation when both fields are null")
    void shouldFailValidationWhenBothFieldsAreNull() {
      var input = new LinkWorkInput(null, null);

      Set<ConstraintViolation<LinkWorkInput>> violations = validator.validate(input);

      assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("should validate with different mangaDexId formats")
    void shouldValidateWithDifferentMangaDexIdFormats() {
      var workId = UUID.randomUUID();

      assertThat(validator.validate(new LinkWorkInput(workId, "manga-123"))).isEmpty();
      assertThat(validator.validate(new LinkWorkInput(workId, "a"))).isEmpty();
      assertThat(validator.validate(new LinkWorkInput(workId, "test-manga-with-long-id-12345"))).isEmpty();
    }
  }
}
