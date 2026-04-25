package dev.williancorrea.manhwa.reader.features.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LibraryStatus")
class LibraryStatusTest {

  @Nested
  @DisplayName("Enum Values")
  class EnumValuesTests {

    @Test
    @DisplayName("should have all required statuses")
    void shouldHaveAllRequiredStatuses() {
      var values = LibraryStatus.values();

      assertThat(values)
          .hasSize(4)
          .contains(
              LibraryStatus.READING,
              LibraryStatus.COMPLETED,
              LibraryStatus.PLAN_TO_READ,
              LibraryStatus.DROPPED
          );
    }

    @Test
    @DisplayName("should have READING status")
    void shouldHaveReadingStatus() {
      assertThat(LibraryStatus.READING).isNotNull();
    }

    @Test
    @DisplayName("should have COMPLETED status")
    void shouldHaveCompletedStatus() {
      assertThat(LibraryStatus.COMPLETED).isNotNull();
    }

    @Test
    @DisplayName("should have PLAN_TO_READ status")
    void shouldHavePlanToReadStatus() {
      assertThat(LibraryStatus.PLAN_TO_READ).isNotNull();
    }

    @Test
    @DisplayName("should have DROPPED status")
    void shouldHaveDroppedStatus() {
      assertThat(LibraryStatus.DROPPED).isNotNull();
    }
  }

  @Nested
  @DisplayName("Name Method")
  class NameMethodTests {

    @Test
    @DisplayName("should return correct name for READING")
    void shouldReturnCorrectNameForReading() {
      assertThat(LibraryStatus.READING.name()).isEqualTo("READING");
    }

    @Test
    @DisplayName("should return correct name for COMPLETED")
    void shouldReturnCorrectNameForCompleted() {
      assertThat(LibraryStatus.COMPLETED.name()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("should return correct name for PLAN_TO_READ")
    void shouldReturnCorrectNameForPlanToRead() {
      assertThat(LibraryStatus.PLAN_TO_READ.name()).isEqualTo("PLAN_TO_READ");
    }

    @Test
    @DisplayName("should return correct name for DROPPED")
    void shouldReturnCorrectNameForDropped() {
      assertThat(LibraryStatus.DROPPED.name()).isEqualTo("DROPPED");
    }
  }

  @Nested
  @DisplayName("Ordinal Method")
  class OrdinalMethodTests {

    @Test
    @DisplayName("should return correct ordinal for each status")
    void shouldReturnCorrectOrdinalForEachStatus() {
      assertThat(LibraryStatus.READING.ordinal()).isEqualTo(0);
      assertThat(LibraryStatus.COMPLETED.ordinal()).isEqualTo(1);
      assertThat(LibraryStatus.PLAN_TO_READ.ordinal()).isEqualTo(2);
      assertThat(LibraryStatus.DROPPED.ordinal()).isEqualTo(3);
    }
  }

  @Nested
  @DisplayName("Enum Comparison")
  class EnumComparisonTests {

    @Test
    @DisplayName("should be equal to itself")
    void shouldBeEqualToItself() {
      assertThat(LibraryStatus.READING).isEqualTo(LibraryStatus.READING);
      assertThat(LibraryStatus.COMPLETED).isEqualTo(LibraryStatus.COMPLETED);
      assertThat(LibraryStatus.PLAN_TO_READ).isEqualTo(LibraryStatus.PLAN_TO_READ);
      assertThat(LibraryStatus.DROPPED).isEqualTo(LibraryStatus.DROPPED);
    }

    @Test
    @DisplayName("should not be equal to different status")
    void shouldNotBeEqualToDifferentStatus() {
      assertThat(LibraryStatus.READING).isNotEqualTo(LibraryStatus.COMPLETED);
      assertThat(LibraryStatus.READING).isNotEqualTo(LibraryStatus.PLAN_TO_READ);
      assertThat(LibraryStatus.COMPLETED).isNotEqualTo(LibraryStatus.DROPPED);
    }
  }

  @Nested
  @DisplayName("ValueOf Method")
  class ValueOfMethodTests {

    @Test
    @DisplayName("should return READING from string")
    void shouldReturnReadingFromString() {
      assertThat(LibraryStatus.valueOf("READING")).isEqualTo(LibraryStatus.READING);
    }

    @Test
    @DisplayName("should return COMPLETED from string")
    void shouldReturnCompletedFromString() {
      assertThat(LibraryStatus.valueOf("COMPLETED")).isEqualTo(LibraryStatus.COMPLETED);
    }

    @Test
    @DisplayName("should return PLAN_TO_READ from string")
    void shouldReturnPlanToReadFromString() {
      assertThat(LibraryStatus.valueOf("PLAN_TO_READ")).isEqualTo(LibraryStatus.PLAN_TO_READ);
    }

    @Test
    @DisplayName("should return DROPPED from string")
    void shouldReturnDroppedFromString() {
      assertThat(LibraryStatus.valueOf("DROPPED")).isEqualTo(LibraryStatus.DROPPED);
    }

    @Test
    @DisplayName("should throw exception for invalid string")
    void shouldThrowExceptionForInvalidString() {
      try {
        LibraryStatus.valueOf("INVALID");
        throw new AssertionError("Expected IllegalArgumentException");
      } catch (IllegalArgumentException e) {
        assertThat(e).isNotNull();
      }
    }
  }

  @Nested
  @DisplayName("Hash Code")
  class HashCodeTests {

    @Test
    @DisplayName("should have consistent hash code")
    void shouldHaveConsistentHashCode() {
      var hashCode1 = LibraryStatus.READING.hashCode();
      var hashCode2 = LibraryStatus.READING.hashCode();

      assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("should have different hash codes for different statuses")
    void shouldHaveDifferentHashCodesForDifferentStatuses() {
      var readingHash = LibraryStatus.READING.hashCode();
      var completedHash = LibraryStatus.COMPLETED.hashCode();

      assertThat(readingHash).isNotEqualTo(completedHash);
    }
  }

  @Nested
  @DisplayName("ToString Method")
  class ToStringMethodTests {

    @Test
    @DisplayName("should return string representation")
    void shouldReturnStringRepresentation() {
      assertThat(LibraryStatus.READING.toString()).isEqualTo("READING");
      assertThat(LibraryStatus.COMPLETED.toString()).isEqualTo("COMPLETED");
      assertThat(LibraryStatus.PLAN_TO_READ.toString()).isEqualTo("PLAN_TO_READ");
      assertThat(LibraryStatus.DROPPED.toString()).isEqualTo("DROPPED");
    }
  }

  @Nested
  @DisplayName("Enum Instance")
  class EnumInstanceTests {

    @Test
    @DisplayName("should be singleton")
    void shouldBeSingleton() {
      var status1 = LibraryStatus.READING;
      var status2 = LibraryStatus.READING;

      assertThat(status1).isSameAs(status2);
    }

    @Test
    @DisplayName("should have same hash code for same instance")
    void shouldHaveSameHashCodeForSameInstance() {
      assertThat(LibraryStatus.READING.hashCode())
          .isEqualTo(LibraryStatus.READING.hashCode());
    }
  }
}
