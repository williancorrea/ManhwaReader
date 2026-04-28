package dev.williancorrea.manhwa.reader.features.page;

import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Page")
class PageTest {

  private UUID pageId;
  private UUID chapterId;
  private Chapter testChapter;

  @BeforeEach
  void setUp() {
    pageId = UUID.randomUUID();
    chapterId = UUID.randomUUID();
    testChapter = Chapter.builder().id(chapterId).build();
  }

  @Nested
  @DisplayName("constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create page with no-arg constructor")
    void shouldCreatePageWithNoArgConstructor() {
      var page = new Page();

      assertThat(page).isNotNull();
      assertThat(page.getId()).isNull();
      assertThat(page.getChapter()).isNull();
      assertThat(page.getPageNumber()).isNull();
      assertThat(page.getFileName()).isNull();
      assertThat(page.getType()).isNull();
      assertThat(page.getContent()).isNull();
      assertThat(page.getDisabled()).isNull();
    }

    @Test
    @DisplayName("should create page with all-args constructor")
    void shouldCreatePageWithAllArgsConstructor() {
      var page = new Page(pageId, testChapter, 1, "page-1.jpg", PageType.IMAGE, "content", false);

      assertThat(page.getId()).isEqualTo(pageId);
      assertThat(page.getChapter()).isEqualTo(testChapter);
      assertThat(page.getPageNumber()).isEqualTo(1);
      assertThat(page.getFileName()).isEqualTo("page-1.jpg");
      assertThat(page.getType()).isEqualTo(PageType.IMAGE);
      assertThat(page.getContent()).isEqualTo("content");
      assertThat(page.getDisabled()).isFalse();
    }
  }

  @Nested
  @DisplayName("builder")
  class BuilderTests {

    @Test
    @DisplayName("should build page with all fields")
    void shouldBuildPageWithAllFields() {
      var page = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .content("image content")
          .disabled(false)
          .build();

      assertThat(page)
          .satisfies(p -> {
            assertThat(p.getId()).isEqualTo(pageId);
            assertThat(p.getChapter()).isEqualTo(testChapter);
            assertThat(p.getPageNumber()).isEqualTo(1);
            assertThat(p.getFileName()).isEqualTo("page-1.jpg");
            assertThat(p.getType()).isEqualTo(PageType.IMAGE);
            assertThat(p.getContent()).isEqualTo("image content");
            assertThat(p.getDisabled()).isFalse();
          });
    }

    @Test
    @DisplayName("should build page with minimal fields")
    void shouldBuildPageWithMinimalFields() {
      var page = Page.builder()
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page)
          .satisfies(p -> {
            assertThat(p.getId()).isNull();
            assertThat(p.getChapter()).isEqualTo(testChapter);
            assertThat(p.getPageNumber()).isEqualTo(1);
            assertThat(p.getFileName()).isEqualTo("page-1.jpg");
            assertThat(p.getType()).isEqualTo(PageType.IMAGE);
            assertThat(p.getContent()).isNull();
            assertThat(p.getDisabled()).isNull();
          });
    }

    @Test
    @DisplayName("should build markdown page")
    void shouldBuildMarkdownPage() {
      var markdownContent = "# Chapter Title\n\nSome content";
      var page = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.md")
          .type(PageType.MARKDOWN)
          .content(markdownContent)
          .disabled(false)
          .build();

      assertThat(page)
          .satisfies(p -> {
            assertThat(p.getType()).isEqualTo(PageType.MARKDOWN);
            assertThat(p.getFileName()).endsWith(".md");
            assertThat(p.getContent()).isEqualTo(markdownContent);
          });
    }

    @Test
    @DisplayName("should build disabled page")
    void shouldBuildDisabledPage() {
      var page = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(true)
          .build();

      assertThat(page.getDisabled()).isTrue();
    }
  }

  @Nested
  @DisplayName("getters and setters")
  class GettersAndSettersTests {

    @Test
    @DisplayName("should get and set id")
    void shouldGetAndSetId() {
      var page = new Page();
      var newId = UUID.randomUUID();

      page.setId(newId);

      assertThat(page.getId()).isEqualTo(newId);
    }

    @Test
    @DisplayName("should get and set chapter")
    void shouldGetAndSetChapter() {
      var page = new Page();
      var newChapter = Chapter.builder().id(UUID.randomUUID()).build();

      page.setChapter(newChapter);

      assertThat(page.getChapter()).isEqualTo(newChapter);
    }

    @Test
    @DisplayName("should get and set page number")
    void shouldGetAndSetPageNumber() {
      var page = new Page();

      page.setPageNumber(5);

      assertThat(page.getPageNumber()).isEqualTo(5);
    }

    @Test
    @DisplayName("should get and set file name")
    void shouldGetAndSetFileName() {
      var page = new Page();

      page.setFileName("new-page-name.jpg");

      assertThat(page.getFileName()).isEqualTo("new-page-name.jpg");
    }

    @Test
    @DisplayName("should get and set type")
    void shouldGetAndSetType() {
      var page = new Page();

      page.setType(PageType.MARKDOWN);

      assertThat(page.getType()).isEqualTo(PageType.MARKDOWN);
    }

    @Test
    @DisplayName("should get and set content")
    void shouldGetAndSetContent() {
      var page = new Page();
      var content = "base64 encoded image content";

      page.setContent(content);

      assertThat(page.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("should get and set disabled flag")
    void shouldGetAndSetDisabledFlag() {
      var page = new Page();

      page.setDisabled(true);

      assertThat(page.getDisabled()).isTrue();

      page.setDisabled(false);

      assertThat(page.getDisabled()).isFalse();
    }
  }

  @Nested
  @DisplayName("equals and hash code")
  class EqualsAndHashCodeTests {

    @Test
    @DisplayName("should be equal when ids are same")
    void shouldBeEqualWhenIdsSame() {
      var page1 = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();
      var page2 = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page1).isEqualTo(page2);
    }

    @Test
    @DisplayName("should have same hash code when equal")
    void shouldHaveSameHashCodeWhenEqual() {
      var page1 = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();
      var page2 = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page1).hasSameHashCodeAs(page2);
    }

    @Test
    @DisplayName("should not be equal when ids are different")
    void shouldNotBeEqualWhenIdsDifferent() {
      var page1 = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page1).isNotEqualTo(page2);
    }
  }

  @Nested
  @DisplayName("serialization")
  class SerializationTests {

    @Test
    @DisplayName("should implement serializable interface")
    void shouldImplementSerializableInterface() {
      var page = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page).isInstanceOf(java.io.Serializable.class);
    }
  }

  @Nested
  @DisplayName("page properties")
  class PagePropertiesTests {

    @Test
    @DisplayName("should have correct page number sequence")
    void shouldHaveCorrectPageNumberSequence() {
      var page1 = Page.builder()
          .pageNumber(1)
          .chapter(testChapter)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();
      var page2 = Page.builder()
          .pageNumber(2)
          .chapter(testChapter)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .build();
      var page3 = Page.builder()
          .pageNumber(3)
          .chapter(testChapter)
          .fileName("page-3.jpg")
          .type(PageType.IMAGE)
          .build();

      assertThat(page1.getPageNumber())
          .isLessThan(page2.getPageNumber())
          .isLessThan(page3.getPageNumber());
    }

    @Test
    @DisplayName("should support different file extensions")
    void shouldSupportDifferentFileExtensions() {
      var jpgPage = Page.builder()
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .build();
      var pngPage = Page.builder()
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.png")
          .type(PageType.IMAGE)
          .build();
      var mdPage = Page.builder()
          .chapter(testChapter)
          .pageNumber(3)
          .fileName("page-3.md")
          .type(PageType.MARKDOWN)
          .build();

      assertThat(jpgPage.getFileName()).endsWith(".jpg");
      assertThat(pngPage.getFileName()).endsWith(".png");
      assertThat(mdPage.getFileName()).endsWith(".md");
    }

    @Test
    @DisplayName("should store large content")
    void shouldStoreLargeContent() {
      var largeContent = "x".repeat(10000);
      var page = Page.builder()
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.txt")
          .type(PageType.MARKDOWN)
          .content(largeContent)
          .build();

      assertThat(page.getContent())
          .hasSize(10000)
          .isEqualTo(largeContent);
    }

    @Test
    @DisplayName("should handle null content")
    void shouldHandleNullContent() {
      var page = Page.builder()
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .content(null)
          .build();

      assertThat(page.getContent()).isNull();
    }
  }
}
