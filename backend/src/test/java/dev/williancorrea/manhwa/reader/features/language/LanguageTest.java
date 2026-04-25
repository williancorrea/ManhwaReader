package dev.williancorrea.manhwa.reader.features.language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Language")
class LanguageTest {

  private UUID languageId;

  @BeforeEach
  void setUp() {
    languageId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("Language Builder")
  class LanguageBuilderTests {

    @Test
    @DisplayName("should create language with id, code, and name")
    void shouldCreateLanguageWithIdCodeAndName() {
      var language = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();

      assertThat(language.getId()).isEqualTo(languageId);
      assertThat(language.getCode()).isEqualTo("en");
      assertThat(language.getName()).isEqualTo("English");
    }

    @Test
    @DisplayName("should create language with flag")
    void shouldCreateLanguageWithFlag() {
      var flag = "🇺🇸";
      var language = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .flag(flag)
          .build();

      assertThat(language.getFlag()).isEqualTo(flag);
    }

    @Test
    @DisplayName("should create language without flag")
    void shouldCreateLanguageWithoutFlag() {
      var language = Language.builder()
          .id(languageId)
          .code("pt")
          .name("Portuguese")
          .build();

      assertThat(language.getFlag()).isNull();
    }
  }

  @Nested
  @DisplayName("Language properties")
  class LanguagePropertiesTests {

    @Test
    @DisplayName("should support getters and setters")
    void shouldSupportGettersAndSetters() {
      var language = new Language();
      language.setId(languageId);
      language.setCode("ja");
      language.setName("Japanese");
      language.setFlag("🇯🇵");

      assertThat(language.getId()).isEqualTo(languageId);
      assertThat(language.getCode()).isEqualTo("ja");
      assertThat(language.getName()).isEqualTo("Japanese");
      assertThat(language.getFlag()).isEqualTo("🇯🇵");
    }

    @Test
    @DisplayName("should support various language codes")
    void shouldSupportVariousLanguageCodes() {
      var englishLanguage = Language.builder()
          .id(UUID.randomUUID())
          .code("en")
          .name("English")
          .build();
      var japaneseLanguage = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();
      var koreanLanguage = Language.builder()
          .id(UUID.randomUUID())
          .code("ko")
          .name("Korean")
          .build();

      assertThat(englishLanguage.getCode()).isEqualTo("en");
      assertThat(japaneseLanguage.getCode()).isEqualTo("ja");
      assertThat(koreanLanguage.getCode()).isEqualTo("ko");
    }
  }

  @Nested
  @DisplayName("Language equality")
  class LanguageEqualityTests {

    @Test
    @DisplayName("should be equal when ids are the same")
    void shouldBeEqualWhenIdsAreSame() {
      var language1 = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();
      var language2 = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();

      assertThat(language1).isEqualTo(language2);
    }

    @Test
    @DisplayName("should not be equal when ids differ")
    void shouldNotBeEqualWhenIdsDiffer() {
      var language1 = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();
      var language2 = Language.builder()
          .id(UUID.randomUUID())
          .code("en")
          .name("English")
          .build();

      assertThat(language1).isNotEqualTo(language2);
    }

    @Test
    @DisplayName("should have same hash code when ids are the same")
    void shouldHaveSameHashCodeWhenIdsAreSame() {
      var language1 = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();
      var language2 = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();

      assertThat(language1).hasSameHashCodeAs(language2);
    }
  }

  @Nested
  @DisplayName("Language serialization")
  class LanguageSerializationTests {

    @Test
    @DisplayName("should implement Serializable")
    void shouldImplementSerializable() {
      var language = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .build();

      assertThat(language).isInstanceOf(java.io.Serializable.class);
    }
  }
}
