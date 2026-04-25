package dev.williancorrea.manhwa.reader.features.chapter.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NextUnreadOutput")
class NextUnreadOutputTest {

  @Nested
  @DisplayName("Record creation")
  class RecordCreationTests {

    @Test
    @DisplayName("should create instance with next unread chapter")
    void shouldCreateInstanceWithNextUnreadChapter() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "5", true);

      assertEquals(chapterId, output.id());
      assertEquals("5", output.numberWithVersion());
      assertTrue(output.hasReadChapters());
    }

    @Test
    @DisplayName("should create instance with no unread chapters")
    void shouldCreateInstanceWithNoUnreadChapters() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, false);

      assertNull(output.id());
      assertNull(output.numberWithVersion());
      assertFalse(output.hasReadChapters());
    }

    @Test
    @DisplayName("should create instance with unread chapters but no reading history")
    void shouldCreateInstanceWithUnreadChaptersButNoHistory() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "1", false);

      assertEquals(chapterId, output.id());
      assertEquals("1", output.numberWithVersion());
      assertFalse(output.hasReadChapters());
    }
  }

  @Nested
  @DisplayName("Reading state")
  class ReadingStateTests {

    @Test
    @DisplayName("should indicate user has read chapters")
    void shouldIndicateUserHasReadChapters() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "10", true);

      assertTrue(output.hasReadChapters());
    }

    @Test
    @DisplayName("should indicate user has not read any chapters")
    void shouldIndicateUserHasNotReadAnyChapters() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "1", false);

      assertFalse(output.hasReadChapters());
    }

    @Test
    @DisplayName("should indicate user has read chapters even when no unread found")
    void shouldIndicateUserHasReadChaptersWhenNoUnread() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, true);

      assertTrue(output.hasReadChapters());
    }

    @Test
    @DisplayName("should indicate user has never read when all unread")
    void shouldIndicateUserHasNeverReadWhenAllUnread() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "1", false);

      assertFalse(output.hasReadChapters());
    }
  }

  @Nested
  @DisplayName("Chapter information")
  class ChapterInformationTests {

    @Test
    @DisplayName("should return chapter ID when unread chapter exists")
    void shouldReturnChapterIdWhenUnreadChapterExists() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "5", true);

      assertEquals(chapterId, output.id());
    }

    @Test
    @DisplayName("should return null chapter ID when no unread chapter")
    void shouldReturnNullChapterIdWhenNoUnread() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, true);

      assertNull(output.id());
    }

    @Test
    @DisplayName("should return chapter number when unread chapter exists")
    void shouldReturnChapterNumberWhenUnreadChapterExists() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "42", true);

      assertEquals("42", output.numberWithVersion());
    }

    @Test
    @DisplayName("should return null chapter number when no unread chapter")
    void shouldReturnNullChapterNumberWhenNoUnread() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, true);

      assertNull(output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle chapter number with version")
    void shouldHandleChapterNumberWithVersion() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "10.5", true);

      assertEquals("10.5", output.numberWithVersion());
    }
  }

  @Nested
  @DisplayName("Frontend integration scenarios")
  class FrontendIntegrationScenariosTests {

    @Test
    @DisplayName("should support continue reading scenario")
    void shouldSupportContinueReadingScenario() {
      UUID nextChapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(nextChapterId, "15", true);

      assertTrue(output.hasReadChapters());
      assertEquals(nextChapterId, output.id());
      assertEquals("15", output.numberWithVersion());
    }

    @Test
    @DisplayName("should support start reading scenario")
    void shouldSupportStartReadingScenario() {
      UUID firstChapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(firstChapterId, "1", false);

      assertFalse(output.hasReadChapters());
      assertEquals(firstChapterId, output.id());
      assertEquals("1", output.numberWithVersion());
    }

    @Test
    @DisplayName("should support finished reading scenario")
    void shouldSupportFinishedReadingScenario() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, true);

      assertTrue(output.hasReadChapters());
      assertNull(output.id());
      assertNull(output.numberWithVersion());
    }

    @Test
    @DisplayName("should support never started scenario")
    void shouldSupportNeverStartedScenario() {
      NextUnreadOutput output = new NextUnreadOutput(null, null, false);

      assertFalse(output.hasReadChapters());
      assertNull(output.id());
      assertNull(output.numberWithVersion());
    }
  }

  @Nested
  @DisplayName("Edge cases")
  class EdgeCasesTests {

    @Test
    @DisplayName("should handle chapter number 0")
    void shouldHandleChapterNumberZero() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "0", false);

      assertEquals("0", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle very large chapter numbers")
    void shouldHandleVeryLargeChapterNumbers() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "999999", true);

      assertEquals("999999", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle decimal chapter numbers")
    void shouldHandleDecimalChapterNumbers() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "5.5", true);

      assertEquals("5.5", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle empty string chapter number")
    void shouldHandleEmptyStringChapterNumber() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "", true);

      assertEquals("", output.numberWithVersion());
    }

    @Test
    @DisplayName("should allow independent chapter ID and number")
    void shouldAllowIndependentChapterIdAndNumber() {
      UUID chapterId = UUID.randomUUID();
      NextUnreadOutput output = new NextUnreadOutput(chapterId, "50", true);

      assertEquals(chapterId, output.id());
      assertEquals("50", output.numberWithVersion());
    }
  }
}
