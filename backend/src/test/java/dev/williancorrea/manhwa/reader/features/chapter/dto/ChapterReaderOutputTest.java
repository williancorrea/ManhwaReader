package dev.williancorrea.manhwa.reader.features.chapter.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChapterReaderOutput")
class ChapterReaderOutputTest {

  @Nested
  @DisplayName("Record creation")
  class RecordCreationTests {

    @Test
    @DisplayName("should create instance with all fields")
    void shouldCreateInstanceWithAllFields() {
      UUID chapterId = UUID.randomUUID();
      UUID previousId = UUID.randomUUID();
      UUID nextId = UUID.randomUUID();
      ChapterNavOutput previous = new ChapterNavOutput(previousId, "1");
      ChapterNavOutput next = new ChapterNavOutput(nextId, "3");
      ChapterPageOutput page = new ChapterPageOutput(1, "IMAGE", "http://image.url", null);

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "2",
          "2",
          "Chapter 2",
          "Work Title",
          "work-slug",
          "MANGA",
          List.of(page),
          previous,
          next
      );

      assertEquals(chapterId, output.id());
      assertEquals("2", output.number());
      assertEquals("2", output.numberWithVersion());
      assertEquals("Chapter 2", output.title());
      assertEquals("Work Title", output.workTitle());
      assertEquals("work-slug", output.workSlug());
      assertEquals("MANGA", output.workType());
      assertEquals(1, output.pages().size());
      assertEquals(previous, output.previousChapter());
      assertEquals(next, output.nextChapter());
    }

    @Test
    @DisplayName("should handle null optional fields")
    void shouldHandleNullOptionalFields() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          null,
          null,
          "work-slug",
          null,
          Collections.emptyList(),
          null,
          null
      );

      assertNull(output.title());
      assertNull(output.workTitle());
      assertNull(output.workType());
      assertNull(output.previousChapter());
      assertNull(output.nextChapter());
    }

    @Test
    @DisplayName("should handle empty page list")
    void shouldHandleEmptyPageList() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals(0, output.pages().size());
    }

    @Test
    @DisplayName("should handle multiple pages")
    void shouldHandleMultiplePages() {
      UUID chapterId = UUID.randomUUID();
      List<ChapterPageOutput> pages = List.of(
          new ChapterPageOutput(1, "IMAGE", "url1", null),
          new ChapterPageOutput(2, "IMAGE", "url2", null),
          new ChapterPageOutput(3, "TEXT", null, "content")
      );

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          "Work",
          "slug",
          "MANGA",
          pages,
          null,
          null
      );

      assertEquals(3, output.pages().size());
      assertEquals("IMAGE", output.pages().get(0).type());
      assertEquals("TEXT", output.pages().get(2).type());
    }

    @Test
    @DisplayName("should correctly map number with version")
    void shouldCorrectlyMapNumberWithVersion() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "50",
          "50.5",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals("50", output.number());
      assertEquals("50.5", output.numberWithVersion());
    }
  }

  @Nested
  @DisplayName("Navigation")
  class NavigationTests {

    @Test
    @DisplayName("should include previous chapter navigation")
    void shouldIncludePreviousChapterNavigation() {
      UUID chapterId = UUID.randomUUID();
      UUID previousId = UUID.randomUUID();
      ChapterNavOutput previous = new ChapterNavOutput(previousId, "1");

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "2",
          "2",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          previous,
          null
      );

      assertEquals(previousId, output.previousChapter().id());
      assertEquals("1", output.previousChapter().numberWithVersion());
    }

    @Test
    @DisplayName("should include next chapter navigation")
    void shouldIncludeNextChapterNavigation() {
      UUID chapterId = UUID.randomUUID();
      UUID nextId = UUID.randomUUID();
      ChapterNavOutput next = new ChapterNavOutput(nextId, "3");

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "2",
          "2",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          next
      );

      assertEquals(nextId, output.nextChapter().id());
      assertEquals("3", output.nextChapter().numberWithVersion());
    }

    @Test
    @DisplayName("should handle both previous and next chapters")
    void shouldHandleBothPreviousAndNextChapters() {
      UUID chapterId = UUID.randomUUID();
      UUID previousId = UUID.randomUUID();
      UUID nextId = UUID.randomUUID();
      ChapterNavOutput previous = new ChapterNavOutput(previousId, "1");
      ChapterNavOutput next = new ChapterNavOutput(nextId, "3");

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "2",
          "2",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          previous,
          next
      );

      assertEquals(previous, output.previousChapter());
      assertEquals(next, output.nextChapter());
    }

    @Test
    @DisplayName("should handle missing navigation (first chapter)")
    void shouldHandleMissingNavigationFirstChapter() {
      UUID chapterId = UUID.randomUUID();
      UUID nextId = UUID.randomUUID();
      ChapterNavOutput next = new ChapterNavOutput(nextId, "2");

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          next
      );

      assertNull(output.previousChapter());
      assertEquals(next, output.nextChapter());
    }

    @Test
    @DisplayName("should handle missing navigation (last chapter)")
    void shouldHandleMissingNavigationLastChapter() {
      UUID chapterId = UUID.randomUUID();
      UUID previousId = UUID.randomUUID();
      ChapterNavOutput previous = new ChapterNavOutput(previousId, "99");

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "100",
          "100",
          "Title",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          previous,
          null
      );

      assertEquals(previous, output.previousChapter());
      assertNull(output.nextChapter());
    }
  }

  @Nested
  @DisplayName("Work information")
  class WorkInformationTests {

    @Test
    @DisplayName("should include work title and slug")
    void shouldIncludeWorkTitleAndSlug() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Chapter Title",
          "Work Title",
          "work-slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals("Work Title", output.workTitle());
      assertEquals("work-slug", output.workSlug());
    }

    @Test
    @DisplayName("should include work type")
    void shouldIncludeWorkType() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          "Work",
          "slug",
          "MANHWA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals("MANHWA", output.workType());
    }

    @Test
    @DisplayName("should handle null work title")
    void shouldHandleNullWorkTitle() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          null,
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertNull(output.workTitle());
    }

    @Test
    @DisplayName("should handle null work type")
    void shouldHandleNullWorkType() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          "Title",
          "Work",
          "slug",
          null,
          Collections.emptyList(),
          null,
          null
      );

      assertNull(output.workType());
    }
  }

  @Nested
  @DisplayName("Chapter information")
  class ChapterInformationTests {

    @Test
    @DisplayName("should include chapter number and title")
    void shouldIncludeChapterNumberAndTitle() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "42",
          "42",
          "Chapter 42: The Answer",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals(chapterId, output.id());
      assertEquals("42", output.number());
      assertEquals("42", output.numberWithVersion());
      assertEquals("Chapter 42: The Answer", output.title());
    }

    @Test
    @DisplayName("should handle chapter with version")
    void shouldHandleChapterWithVersion() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "10",
          "10.5",
          "Revised Chapter",
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertEquals("10", output.number());
      assertEquals("10.5", output.numberWithVersion());
    }

    @Test
    @DisplayName("should handle null chapter title")
    void shouldHandleNullChapterTitle() {
      UUID chapterId = UUID.randomUUID();

      ChapterReaderOutput output = new ChapterReaderOutput(
          chapterId,
          "1",
          "1",
          null,
          "Work",
          "slug",
          "MANGA",
          Collections.emptyList(),
          null,
          null
      );

      assertNull(output.title());
    }
  }
}
