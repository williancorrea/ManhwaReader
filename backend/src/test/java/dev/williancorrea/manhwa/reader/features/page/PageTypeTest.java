package dev.williancorrea.manhwa.reader.features.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PageType")
class PageTypeTest {

  @Nested
  @DisplayName("enum values")
  class EnumValuesTests {

    @Test
    @DisplayName("should have IMAGE type")
    void shouldHaveImageType() {
      assertThat(PageType.IMAGE).isNotNull();
      assertThat(PageType.IMAGE.name()).isEqualTo("IMAGE");
    }

    @Test
    @DisplayName("should have MARKDOWN type")
    void shouldHaveMarkdownType() {
      assertThat(PageType.MARKDOWN).isNotNull();
      assertThat(PageType.MARKDOWN.name()).isEqualTo("MARKDOWN");
    }

    @Test
    @DisplayName("should have exactly two enum values")
    void shouldHaveExactlyTwoEnumValues() {
      var values = PageType.values();

      assertThat(values)
          .hasSize(2)
          .contains(PageType.IMAGE, PageType.MARKDOWN);
    }

    @Test
    @DisplayName("should be able to get type by name")
    void shouldBeAbleToGetTypeByName() {
      var imageType = PageType.valueOf("IMAGE");
      var markdownType = PageType.valueOf("MARKDOWN");

      assertThat(imageType).isEqualTo(PageType.IMAGE);
      assertThat(markdownType).isEqualTo(PageType.MARKDOWN);
    }

    @Test
    @DisplayName("should have distinct enum values")
    void shouldHaveDistinctEnumValues() {
      assertThat(PageType.IMAGE).isNotEqualTo(PageType.MARKDOWN);
    }
  }
}
