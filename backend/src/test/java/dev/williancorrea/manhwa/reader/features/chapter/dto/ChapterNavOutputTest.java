package dev.williancorrea.manhwa.reader.features.chapter.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChapterNavOutput")
class ChapterNavOutputTest {

  @Nested
  @DisplayName("Record creation")
  class RecordCreationTests {

    @Test
    @DisplayName("should create instance with id and number")
    void shouldCreateInstanceWithIdAndNumber() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "5");

      assertEquals(id, output.id());
      assertEquals("5", output.numberWithVersion());
    }

    @Test
    @DisplayName("should create instance with version number")
    void shouldCreateInstanceWithVersionNumber() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "5.2");

      assertEquals(id, output.id());
      assertEquals("5.2", output.numberWithVersion());
    }

    @Test
    @DisplayName("should create instance with large chapter number")
    void shouldCreateInstanceWithLargeChapterNumber() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "999");

      assertEquals("999", output.numberWithVersion());
    }

    @Test
    @DisplayName("should create instance with large version number")
    void shouldCreateInstanceWithLargeVersionNumber() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "999.99");

      assertEquals("999.99", output.numberWithVersion());
    }
  }

  @Nested
  @DisplayName("Navigation context")
  class NavigationContextTests {

    @Test
    @DisplayName("should represent previous chapter")
    void shouldRepresentPreviousChapter() {
      UUID previousId = UUID.randomUUID();
      ChapterNavOutput previous = new ChapterNavOutput(previousId, "4");

      assertEquals(previousId, previous.id());
      assertEquals("4", previous.numberWithVersion());
    }

    @Test
    @DisplayName("should represent next chapter")
    void shouldRepresentNextChapter() {
      UUID nextId = UUID.randomUUID();
      ChapterNavOutput next = new ChapterNavOutput(nextId, "6");

      assertEquals(nextId, next.id());
      assertEquals("6", next.numberWithVersion());
    }

    @Test
    @DisplayName("should represent first chapter")
    void shouldRepresentFirstChapter() {
      UUID firstId = UUID.randomUUID();
      ChapterNavOutput first = new ChapterNavOutput(firstId, "1");

      assertEquals("1", first.numberWithVersion());
    }

    @Test
    @DisplayName("should represent chapter with version")
    void shouldRepresentChapterWithVersion() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "10.5");

      assertEquals("10.5", output.numberWithVersion());
    }
  }

  @Nested
  @DisplayName("ID uniqueness")
  class IdUniquenessTests {

    @Test
    @DisplayName("should maintain unique IDs for different chapters")
    void shouldMaintainUniqueIDs() {
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      ChapterNavOutput chapter1 = new ChapterNavOutput(id1, "1");
      ChapterNavOutput chapter2 = new ChapterNavOutput(id2, "2");

      assertEquals(id1, chapter1.id());
      assertEquals(id2, chapter2.id());
    }

    @Test
    @DisplayName("should allow same number for different chapter IDs")
    void shouldAllowSameNumberForDifferentIds() {
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      ChapterNavOutput chapter1 = new ChapterNavOutput(id1, "5");
      ChapterNavOutput chapter2 = new ChapterNavOutput(id2, "5");

      assertEquals(chapter1.numberWithVersion(), chapter2.numberWithVersion());
      assertIdsDifferent(chapter1.id(), chapter2.id());
    }

    private void assertIdsDifferent(UUID id1, UUID id2) {
      assertEquals(false, id1.equals(id2));
    }
  }

  @Nested
  @DisplayName("Number formatting")
  class NumberFormattingTests {

    @Test
    @DisplayName("should preserve number format as provided")
    void shouldPreserveNumberFormat() {
      UUID id = UUID.randomUUID();
      String numberWithVersion = "42.1";
      ChapterNavOutput output = new ChapterNavOutput(id, numberWithVersion);

      assertEquals(numberWithVersion, output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle single digit numbers")
    void shouldHandleSingleDigitNumbers() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "1");

      assertEquals("1", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle decimal numbers")
    void shouldHandleDecimalNumbers() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "1.5");

      assertEquals("1.5", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle zero chapter number")
    void shouldHandleZeroChapterNumber() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output = new ChapterNavOutput(id, "0");

      assertEquals("0", output.numberWithVersion());
    }

    @Test
    @DisplayName("should preserve any number string")
    void shouldPreserveAnyNumberString() {
      UUID id = UUID.randomUUID();
      ChapterNavOutput output1 = new ChapterNavOutput(id, "100");
      ChapterNavOutput output2 = new ChapterNavOutput(id, "0.5");
      ChapterNavOutput output3 = new ChapterNavOutput(id, "999.99");

      assertEquals("100", output1.numberWithVersion());
      assertEquals("0.5", output2.numberWithVersion());
      assertEquals("999.99", output3.numberWithVersion());
    }
  }
}
