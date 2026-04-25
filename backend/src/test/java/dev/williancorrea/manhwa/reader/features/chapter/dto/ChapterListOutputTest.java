package dev.williancorrea.manhwa.reader.features.chapter.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgress;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.volume.Volume;
import dev.williancorrea.manhwa.reader.features.work.Work;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChapterListOutput")
class ChapterListOutputTest {

  private UUID chapterId;
  private Work testWork;
  private Language testLanguage;
  private Scanlator testScanlator;
  private Volume testVolume;

  @BeforeEach
  void setUp() {
    chapterId = UUID.randomUUID();
    testWork = Work.builder().id(UUID.randomUUID()).build();
    testLanguage = Language.builder()
        .id(UUID.randomUUID())
        .code("EN")
        .build();
    testScanlator = Scanlator.builder()
        .id(UUID.randomUUID())
        .name("TestScanlator")
        .build();
    testVolume = Volume.builder()
        .id(UUID.randomUUID())
        .number(1)
        .build();
  }

  @Nested
  @DisplayName("fromEntity()")
  class FromEntityTests {

    @Test
    @DisplayName("should convert chapter entity to DTO without progress")
    void shouldConvertChapterEntityToDto() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("123")
          .numberFormatted("0123")
          .numberVersion("0001")
          .title("Chapter Title")
          .language(testLanguage)
          .releaseDate(LocalDate.of(2023, 1, 1))
          .volume(testVolume)
          .scanlator(testScanlator)
          .publishedAt(OffsetDateTime.now())
          .synced(true)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertEquals(chapterId, output.id());
      assertEquals("123", output.number());
      assertEquals("0123", output.numberFormatted());
      assertEquals("123.1", output.numberWithVersion());
      assertEquals("Chapter Title", output.title());
      assertEquals("EN", output.language());
      assertEquals(LocalDate.of(2023, 1, 1), output.releaseDate());
      assertEquals("TestScanlator", output.scanlator());
      assertEquals(1, output.volume());
      assertFalse(output.isRead());
      assertEquals(0, output.readProgress());
      assertTrue(output.synced());
    }

    @Test
    @DisplayName("should map isRead to true when progress exists")
    void shouldMapIsReadToTrueWhenProgressExists() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      ReadingProgress progress = ReadingProgress.builder()
          .id(UUID.randomUUID())
          .chapter(chapter)
          .pageNumber(10)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, progress);

      assertTrue(output.isRead());
      assertEquals(10, output.readProgress());
    }

    @Test
    @DisplayName("should set readProgress to 0 when progress has no page number")
    void shouldSetReadProgressToZeroWhenNoPageNumber() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      ReadingProgress progress = ReadingProgress.builder()
          .id(UUID.randomUUID())
          .chapter(chapter)
          .pageNumber(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, progress);

      assertTrue(output.isRead());
      assertEquals(0, output.readProgress());
    }

    @Test
    @DisplayName("should handle null language")
    void shouldHandleNullLanguage() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(null)
          .scanlator(testScanlator)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.language());
    }

    @Test
    @DisplayName("should handle null scanlator")
    void shouldHandleNullScanlator() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.scanlator());
    }

    @Test
    @DisplayName("should handle null volume")
    void shouldHandleNullVolume() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .volume(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.volume());
    }

    @Test
    @DisplayName("should handle null release date")
    void shouldHandleNullReleaseDate() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .releaseDate(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.releaseDate());
    }

    @Test
    @DisplayName("should handle null title")
    void shouldHandleNullTitle() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .title(null)
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.title());
    }

    @Test
    @DisplayName("should handle null published at")
    void shouldHandleNullPublishedAt() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .publishedAt(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.publishedAt());
    }

    @Test
    @DisplayName("should handle null synced")
    void shouldHandleNullSynced() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .synced(null)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertNull(output.synced());
    }

    @Test
    @DisplayName("should correctly format number with version when version is not 0000")
    void shouldCorrectlyFormatNumberWithVersion() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("50")
          .numberFormatted("0050")
          .numberVersion("0005")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertEquals("50.5", output.numberWithVersion());
    }

    @Test
    @DisplayName("should correctly format number without version when version is 0000")
    void shouldCorrectlyFormatNumberWithoutVersion() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("25")
          .numberFormatted("0025")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertEquals("25", output.numberWithVersion());
    }

    @Test
    @DisplayName("should map false synced value")
    void shouldMapFalseSyncedValue() {
      Chapter chapter = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .synced(false)
          .build();

      ChapterListOutput output = ChapterListOutput.fromEntity(chapter, null);

      assertFalse(output.synced());
    }
  }

  @Nested
  @DisplayName("Record properties")
  class RecordPropertiesTests {

    @Test
    @DisplayName("should create instance with all fields")
    void shouldCreateInstanceWithAllFields() {
      LocalDate releaseDate = LocalDate.now();
      OffsetDateTime publishedAt = OffsetDateTime.now();

      ChapterListOutput output = new ChapterListOutput(
          chapterId,
          "1",
          "0001",
          "1",
          "Title",
          "EN",
          releaseDate,
          "Scanlator",
          1,
          true,
          5,
          publishedAt,
          true
      );

      assertEquals(chapterId, output.id());
      assertEquals("1", output.number());
      assertEquals("0001", output.numberFormatted());
      assertEquals("1", output.numberWithVersion());
      assertEquals("Title", output.title());
      assertEquals("EN", output.language());
      assertEquals(releaseDate, output.releaseDate());
      assertEquals("Scanlator", output.scanlator());
      assertEquals(1, output.volume());
      assertTrue(output.isRead());
      assertEquals(5, output.readProgress());
      assertEquals(publishedAt, output.publishedAt());
      assertTrue(output.synced());
    }
  }
}
