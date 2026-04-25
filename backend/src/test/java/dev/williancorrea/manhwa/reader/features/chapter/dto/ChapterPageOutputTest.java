package dev.williancorrea.manhwa.reader.features.chapter.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChapterPageOutput")
class ChapterPageOutputTest {

  @Nested
  @DisplayName("Record creation")
  class RecordCreationTests {

    @Test
    @DisplayName("should create image page with URL")
    void shouldCreateImagePageWithUrl() {
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", "https://example.com/image.jpg", null);

      assertEquals(1, output.pageNumber());
      assertEquals("IMAGE", output.type());
      assertEquals("https://example.com/image.jpg", output.imageUrl());
      assertNull(output.content());
    }

    @Test
    @DisplayName("should create text page with content")
    void shouldCreateTextPageWithContent() {
      String content = "Chapter content goes here";
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, content);

      assertEquals(1, output.pageNumber());
      assertEquals("TEXT", output.type());
      assertNull(output.imageUrl());
      assertEquals(content, output.content());
    }

    @Test
    @DisplayName("should handle page with both URL and content")
    void shouldHandlePageWithBothUrlAndContent() {
      ChapterPageOutput output = new ChapterPageOutput(1, "HYBRID", "url", "content");

      assertEquals("HYBRID", output.type());
      assertEquals("url", output.imageUrl());
      assertEquals("content", output.content());
    }

    @Test
    @DisplayName("should handle page with neither URL nor content")
    void shouldHandlePageWithNeitherUrlNorContent() {
      ChapterPageOutput output = new ChapterPageOutput(1, "UNKNOWN", null, null);

      assertEquals(1, output.pageNumber());
      assertEquals("UNKNOWN", output.type());
      assertNull(output.imageUrl());
      assertNull(output.content());
    }
  }

  @Nested
  @DisplayName("Page numbering")
  class PageNumberingTests {

    @Test
    @DisplayName("should handle first page")
    void shouldHandleFirstPage() {
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", "url", null);
      assertEquals(1, output.pageNumber());
    }

    @Test
    @DisplayName("should handle multiple pages with correct numbering")
    void shouldHandleMultiplePages() {
      ChapterPageOutput page1 = new ChapterPageOutput(1, "IMAGE", "url1", null);
      ChapterPageOutput page2 = new ChapterPageOutput(2, "IMAGE", "url2", null);
      ChapterPageOutput page10 = new ChapterPageOutput(10, "IMAGE", "url10", null);

      assertEquals(1, page1.pageNumber());
      assertEquals(2, page2.pageNumber());
      assertEquals(10, page10.pageNumber());
    }
  }

  @Nested
  @DisplayName("Type handling")
  class TypeHandlingTests {

    @Test
    @DisplayName("should handle IMAGE type")
    void shouldHandleImageType() {
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", "url", null);
      assertEquals("IMAGE", output.type());
    }

    @Test
    @DisplayName("should handle TEXT type")
    void shouldHandleTextType() {
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, "content");
      assertEquals("TEXT", output.type());
    }

    @Test
    @DisplayName("should handle custom type")
    void shouldHandleCustomType() {
      ChapterPageOutput output = new ChapterPageOutput(1, "CUSTOM_TYPE", null, null);
      assertEquals("CUSTOM_TYPE", output.type());
    }
  }

  @Nested
  @DisplayName("Content handling")
  class ContentHandlingTests {

    @Test
    @DisplayName("should handle large content string")
    void shouldHandleLargeContentString() {
      String largeContent = "a".repeat(10000);
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, largeContent);

      assertEquals(largeContent, output.content());
      assertEquals(10000, output.content().length());
    }

    @Test
    @DisplayName("should handle content with special characters")
    void shouldHandleContentWithSpecialCharacters() {
      String content = "Content with <tags> & \"quotes\" and \\escapes\\";
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, content);

      assertEquals(content, output.content());
    }

    @Test
    @DisplayName("should handle multiline content")
    void shouldHandleMultilineContent() {
      String content = "Line 1\nLine 2\nLine 3";
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, content);

      assertEquals(content, output.content());
    }

    @Test
    @DisplayName("should handle empty content")
    void shouldHandleEmptyContent() {
      ChapterPageOutput output = new ChapterPageOutput(1, "TEXT", null, "");
      assertEquals("", output.content());
    }
  }

  @Nested
  @DisplayName("URL handling")
  class UrlHandlingTests {

    @Test
    @DisplayName("should handle full URL")
    void shouldHandleFullUrl() {
      String url = "https://storage.example.com/path/to/image.jpg?size=large&format=webp";
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", url, null);

      assertEquals(url, output.imageUrl());
    }

    @Test
    @DisplayName("should handle relative URL")
    void shouldHandleRelativeUrl() {
      String url = "/storage/image.jpg";
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", url, null);

      assertEquals(url, output.imageUrl());
    }

    @Test
    @DisplayName("should handle empty URL")
    void shouldHandleEmptyUrl() {
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", "", null);
      assertEquals("", output.imageUrl());
    }

    @Test
    @DisplayName("should handle presigned URL with token")
    void shouldHandlePresignedUrl() {
      String url = "https://storage.example.com/image.jpg?token=abc123&expires=1704067200";
      ChapterPageOutput output = new ChapterPageOutput(1, "IMAGE", url, null);

      assertEquals(url, output.imageUrl());
    }
  }
}
