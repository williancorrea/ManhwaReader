package dev.williancorrea.manhwa.reader.features.tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagGroupType")
class TagGroupTypeTest {

  @Nested
  @DisplayName("Enum values")
  class EnumValuesTests {

    @Test
    @DisplayName("should have THEME value")
    void shouldHaveThemeValue() {
      assertThat(TagGroupType.THEME).isNotNull();
      assertThat(TagGroupType.THEME.name()).isEqualTo("THEME");
    }

    @Test
    @DisplayName("should have GENRE value")
    void shouldHaveGenreValue() {
      assertThat(TagGroupType.GENRE).isNotNull();
      assertThat(TagGroupType.GENRE.name()).isEqualTo("GENRE");
    }

    @Test
    @DisplayName("should have FORMAT value")
    void shouldHaveFormatValue() {
      assertThat(TagGroupType.FORMAT).isNotNull();
      assertThat(TagGroupType.FORMAT.name()).isEqualTo("FORMAT");
    }

    @Test
    @DisplayName("should have CONTENT value")
    void shouldHaveContentValue() {
      assertThat(TagGroupType.CONTENT).isNotNull();
      assertThat(TagGroupType.CONTENT.name()).isEqualTo("CONTENT");
    }

    @Test
    @DisplayName("should have four enum values")
    void shouldHaveFourEnumValues() {
      var values = TagGroupType.values();

      assertThat(values).hasSize(4);
      assertThat(values).contains(TagGroupType.THEME, TagGroupType.GENRE, TagGroupType.FORMAT, TagGroupType.CONTENT);
    }
  }

  @Nested
  @DisplayName("Enum operations")
  class EnumOperationsTests {

    @Test
    @DisplayName("should convert enum to string")
    void shouldConvertEnumToString() {
      assertThat(TagGroupType.THEME.toString()).isEqualTo("THEME");
      assertThat(TagGroupType.GENRE.toString()).isEqualTo("GENRE");
      assertThat(TagGroupType.FORMAT.toString()).isEqualTo("FORMAT");
      assertThat(TagGroupType.CONTENT.toString()).isEqualTo("CONTENT");
    }

    @Test
    @DisplayName("should get enum by name")
    void shouldGetEnumByName() {
      var theme = TagGroupType.valueOf("THEME");
      var genre = TagGroupType.valueOf("GENRE");
      var format = TagGroupType.valueOf("FORMAT");
      var content = TagGroupType.valueOf("CONTENT");

      assertThat(theme).isEqualTo(TagGroupType.THEME);
      assertThat(genre).isEqualTo(TagGroupType.GENRE);
      assertThat(format).isEqualTo(TagGroupType.FORMAT);
      assertThat(content).isEqualTo(TagGroupType.CONTENT);
    }
  }
}
