package dev.williancorrea.manhwa.reader.features.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.volume.Volume;
import dev.williancorrea.manhwa.reader.features.work.Work;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Chapter")
class ChapterTest {

  @Nested
  @DisplayName("setNumberFormatted() and getNumberFormatted()")
  class NumberFormattedTests {

    @Test
    @DisplayName("should pad number with leading zeros to 4 digits")
    void shouldPadNumberWithLeadingZeros() {
      Chapter chapter = new Chapter();
      chapter.setNumberFormatted("1");
      assertEquals("0001", chapter.getNumberFormatted());
    }

    @Test
    @DisplayName("should pad two-digit number with leading zeros")
    void shouldPadTwoDigitNumber() {
      Chapter chapter = new Chapter();
      chapter.setNumberFormatted("42");
      assertEquals("0042", chapter.getNumberFormatted());
    }

    @Test
    @DisplayName("should preserve four-digit number")
    void shouldPreserveFourDigitNumber() {
      Chapter chapter = new Chapter();
      chapter.setNumberFormatted("1234");
      assertEquals("1234", chapter.getNumberFormatted());
    }

    @Test
    @DisplayName("should handle number with whitespace")
    void shouldHandleNumberWithWhitespace() {
      Chapter chapter = new Chapter();
      chapter.setNumberFormatted("  5  ");
      assertEquals("0005", chapter.getNumberFormatted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "00"})
    @DisplayName("should handle edge case numbers")
    void shouldHandleEdgeCaseNumbers(String input) {
      Chapter chapter = new Chapter();
      chapter.setNumberFormatted(input);
      assertNotNull(chapter.getNumberFormatted());
    }
  }

  @Nested
  @DisplayName("getNumberWithVersionInteger()")
  class NumberWithVersionIntegerTests {

    @Test
    @DisplayName("should return chapter number without version when version is 0000")
    void shouldReturnNumberWithoutVersionWhenVersionIsZero() {
      Chapter chapter = Chapter.builder()
          .numberFormatted("0123")
          .numberVersion("0000")
          .build();
      assertEquals("123", chapter.getNumberWithVersionInteger());
    }

    @Test
    @DisplayName("should return chapter number with version when version is not 0000")
    void shouldReturnNumberWithVersionWhenVersionExists() {
      Chapter chapter = Chapter.builder()
          .numberFormatted("0123")
          .numberVersion("0001")
          .build();
      assertEquals("123.1", chapter.getNumberWithVersionInteger());
    }

    @Test
    @DisplayName("should handle version with multiple digits")
    void shouldHandleVersionWithMultipleDigits() {
      Chapter chapter = Chapter.builder()
          .numberFormatted("0050")
          .numberVersion("0025")
          .build();
      assertEquals("50.25", chapter.getNumberWithVersionInteger());
    }

    @Test
    @DisplayName("should handle chapter number 0")
    void shouldHandleChapterNumberZero() {
      Chapter chapter = Chapter.builder()
          .numberFormatted("0000")
          .numberVersion("0000")
          .build();
      assertEquals("0", chapter.getNumberWithVersionInteger());
    }

    @Test
    @DisplayName("should handle single digit version")
    void shouldHandleSingleDigitVersion() {
      Chapter chapter = Chapter.builder()
          .numberFormatted("0100")
          .numberVersion("0005")
          .build();
      assertEquals("100.5", chapter.getNumberWithVersionInteger());
    }
  }

  @Nested
  @DisplayName("Entity properties")
  class EntityPropertiesTests {

    @Test
    @DisplayName("should build complete chapter entity with all fields")
    void shouldBuildCompleteChapter() {
      UUID chapterId = UUID.randomUUID();
      UUID workId = UUID.randomUUID();
      Work work = Work.builder().id(workId).build();
      Language language = Language.builder().id(UUID.randomUUID()).code("EN").build();
      Scanlator scanlator = Scanlator.builder().id(UUID.randomUUID()).name("Scan").build();
      Volume volume = Volume.builder().id(UUID.randomUUID()).number(1).build();
      LocalDate releaseDate = LocalDate.of(2023, 1, 1);
      OffsetDateTime now = OffsetDateTime.now();

      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(work)
          .number("123")
          .numberFormatted("0123")
          .numberVersion("0001")
          .title("Chapter Title")
          .language(language)
          .releaseDate(releaseDate)
          .volume(volume)
          .scanlator(scanlator)
          .synced(true)
          .disabled(false)
          .createdAt(now)
          .publishedAt(now)
          .build();

      assertEquals(chapterId, chapter.getId());
      assertEquals(work, chapter.getWork());
      assertEquals("123", chapter.getNumber());
      assertEquals("0123", chapter.getNumberFormatted());
      assertEquals("0001", chapter.getNumberVersion());
      assertEquals("Chapter Title", chapter.getTitle());
      assertEquals(language, chapter.getLanguage());
      assertEquals(releaseDate, chapter.getReleaseDate());
      assertEquals(volume, chapter.getVolume());
      assertEquals(scanlator, chapter.getScanlator());
      assertTrue(chapter.getSynced());
      assertEquals(false, chapter.getDisabled());
      assertEquals(now, chapter.getCreatedAt());
      assertEquals(now, chapter.getPublishedAt());
    }

    @Test
    @DisplayName("should allow null optional fields")
    void shouldAllowNullOptionalFields() {
      Chapter chapter = Chapter.builder()
          .id(UUID.randomUUID())
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      assertNull(chapter.getTitle());
      assertNull(chapter.getReleaseDate());
      assertNull(chapter.getVolume());
      assertNull(chapter.getSynced());
      assertNull(chapter.getDisabled());
      assertNull(chapter.getCreatedAt());
      assertNull(chapter.getPublishedAt());
    }

    @Test
    @DisplayName("should use UUID strategy for id generation")
    void shouldUseUuidStrategyForIdGeneration() {
      Chapter chapter1 = Chapter.builder()
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      Chapter chapter2 = Chapter.builder()
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      assertNull(chapter1.getId());
      assertNull(chapter2.getId());
    }
  }

  @Nested
  @DisplayName("Equality and Hashing")
  class EqualityTests {

    @Test
    @DisplayName("should consider chapters equal if they have the same id")
    void shouldConsiderChaptersEqualByIdOnly() {
      UUID sharedId = UUID.randomUUID();
      UUID workId = UUID.randomUUID();

      Chapter chapter1 = Chapter.builder()
          .id(sharedId)
          .work(Work.builder().id(workId).build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      Chapter chapter2 = Chapter.builder()
          .id(sharedId)
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0001")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      assertEquals(chapter1, chapter2);
    }

    @Test
    @DisplayName("should have same hash for chapters with same id")
    void shouldHaveSameHashForChaptersWithSameId() {
      UUID sharedId = UUID.randomUUID();
      UUID workId = UUID.randomUUID();

      Chapter chapter1 = Chapter.builder()
          .id(sharedId)
          .work(Work.builder().id(workId).build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      Chapter chapter2 = Chapter.builder()
          .id(sharedId)
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0001")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      assertEquals(chapter1.hashCode(), chapter2.hashCode());
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      Chapter chapter = Chapter.builder()
          .id(UUID.randomUUID())
          .work(Work.builder().id(UUID.randomUUID()).build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).build())
          .build();

      assertTrue(chapter instanceof java.io.Serializable);
    }
  }
}
