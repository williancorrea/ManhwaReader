package dev.williancorrea.manhwa.reader.features.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChapterService")
class ChapterServiceTest {

  @Mock
  private ChapterRepository repository;

  @InjectMocks
  private ChapterService chapterService;

  private UUID workId;
  private UUID chapterId;
  private Chapter testChapter;
  private Work testWork;
  private Language testLanguage;
  private Scanlator testScanlator;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    chapterId = UUID.randomUUID();
    testWork = Work.builder().id(workId).build();
    testLanguage = Language.builder().id(UUID.randomUUID()).code("EN").build();
    testScanlator = Scanlator.builder().id(UUID.randomUUID()).name("Scan").build();
    testChapter = Chapter.builder()
        .id(chapterId)
        .work(testWork)
        .number("1")
        .numberFormatted("0001")
        .numberVersion("0000")
        .language(testLanguage)
        .scanlator(testScanlator)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all chapters")
    void shouldReturnAllChapters() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(repository.findAll()).thenReturn(List.of(testChapter, chapter2));

      List<Chapter> result = chapterService.findAll();

      assertEquals(2, result.size());
      assertTrue(result.contains(testChapter));
      assertTrue(result.contains(chapter2));
      verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return empty list when no chapters exist")
    void shouldReturnEmptyListWhenNoChaptersExist() {
      when(repository.findAll()).thenReturn(Collections.emptyList());

      List<Chapter> result = chapterService.findAll();

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return chapter when found")
    void shouldReturnChapterWhenFound() {
      when(repository.findById(chapterId)).thenReturn(Optional.of(testChapter));

      Optional<Chapter> result = chapterService.findById(chapterId);

      assertTrue(result.isPresent());
      assertEquals(testChapter, result.get());
      verify(repository, times(1)).findById(chapterId);
    }

    @Test
    @DisplayName("should return empty when chapter not found")
    void shouldReturnEmptyWhenChapterNotFound() {
      when(repository.findById(chapterId)).thenReturn(Optional.empty());

      Optional<Chapter> result = chapterService.findById(chapterId);

      assertFalse(result.isPresent());
      verify(repository, times(1)).findById(chapterId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return chapter")
    void shouldSaveAndReturnChapter() {
      when(repository.save(testChapter)).thenReturn(testChapter);

      Chapter result = chapterService.save(testChapter);

      assertEquals(testChapter, result);
      verify(repository, times(1)).save(testChapter);
    }

    @Test
    @DisplayName("should save chapter with all fields")
    void shouldSaveChapterWithAllFields() {
      when(repository.save(testChapter)).thenReturn(testChapter);

      chapterService.save(testChapter);

      verify(repository, times(1)).save(testChapter);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when chapter exists")
    void shouldReturnTrueWhenChapterExists() {
      when(repository.existsById(chapterId)).thenReturn(true);

      boolean result = chapterService.existsById(chapterId);

      assertTrue(result);
      verify(repository, times(1)).existsById(chapterId);
    }

    @Test
    @DisplayName("should return false when chapter does not exist")
    void shouldReturnFalseWhenChapterDoesNotExist() {
      when(repository.existsById(chapterId)).thenReturn(false);

      boolean result = chapterService.existsById(chapterId);

      assertFalse(result);
      verify(repository, times(1)).existsById(chapterId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete chapter by id")
    void shouldDeleteChapterById() {
      chapterService.deleteById(chapterId);

      verify(repository, times(1)).deleteById(chapterId);
    }
  }

  @Nested
  @DisplayName("findAllByWorkId()")
  class FindAllByWorkIdTests {

    @Test
    @DisplayName("should return all chapters for a work")
    void shouldReturnAllChaptersForWork() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(repository.findAllByWork_Id(workId)).thenReturn(List.of(testChapter, chapter2));

      List<Chapter> result = chapterService.findAllByWorkId(workId);

      assertEquals(2, result.size());
      verify(repository, times(1)).findAllByWork_Id(workId);
    }

    @Test
    @DisplayName("should return empty list when work has no chapters")
    void shouldReturnEmptyListWhenWorkHasNoChapters() {
      when(repository.findAllByWork_Id(workId)).thenReturn(Collections.emptyList());

      List<Chapter> result = chapterService.findAllByWorkId(workId);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAllByWork_Id(workId);
    }
  }

  @Nested
  @DisplayName("findChaptersUpTo()")
  class FindChaptersUpToTests {

    @Test
    @DisplayName("should return chapters up to specified chapter number")
    void shouldReturnChaptersUpToSpecifiedNumber() {
      Chapter chapter1 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      Chapter chapter3 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("3")
          .numberFormatted("0003")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(repository.findChaptersUpTo(workId, "0002", "0000"))
          .thenReturn(List.of(chapter1, testChapter));

      List<Chapter> result = chapterService.findChaptersUpTo(workId, "0002", "0000");

      assertEquals(2, result.size());
      verify(repository, times(1)).findChaptersUpTo(workId, "0002", "0000");
    }

    @Test
    @DisplayName("should return empty list when no chapters found")
    void shouldReturnEmptyListWhenNoChaptersFound() {
      when(repository.findChaptersUpTo(workId, "0100", "0000"))
          .thenReturn(Collections.emptyList());

      List<Chapter> result = chapterService.findChaptersUpTo(workId, "0100", "0000");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findChaptersFrom()")
  class FindChaptersFromTests {

    @Test
    @DisplayName("should return chapters from specified chapter number")
    void shouldReturnChaptersFromSpecifiedNumber() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      Chapter chapter3 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("3")
          .numberFormatted("0003")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(repository.findChaptersFrom(workId, "0001", "0000"))
          .thenReturn(List.of(testChapter, chapter2, chapter3));

      List<Chapter> result = chapterService.findChaptersFrom(workId, "0001", "0000");

      assertEquals(3, result.size());
      verify(repository, times(1)).findChaptersFrom(workId, "0001", "0000");
    }
  }

  @Nested
  @DisplayName("findByNumberAndWorkIdAndScanlatorId()")
  class FindByNumberAndWorkIdAndScanlatorIdTests {

    @Test
    @DisplayName("should return chapter when found")
    void shouldReturnChapterWhenFound() {
      when(repository.findByNumberAndWorkIdAndScanlatorIdAndLanguageId(
          "0001", "0000", workId, testScanlator.getId(), testLanguage.getId()))
          .thenReturn(Optional.of(testChapter));

      Optional<Chapter> result = chapterService.findByNumberAndWorkIdAndScanlatorId(
          "0001", "0000", testWork, testScanlator, testLanguage);

      assertTrue(result.isPresent());
      assertEquals(testChapter, result.get());
    }

    @Test
    @DisplayName("should return empty when chapter not found")
    void shouldReturnEmptyWhenChapterNotFound() {
      when(repository.findByNumberAndWorkIdAndScanlatorIdAndLanguageId(
          "0999", "0000", workId, testScanlator.getId(), testLanguage.getId()))
          .thenReturn(Optional.empty());

      Optional<Chapter> result = chapterService.findByNumberAndWorkIdAndScanlatorId(
          "0999", "0000", testWork, testScanlator, testLanguage);

      assertFalse(result.isPresent());
    }
  }

  @Nested
  @DisplayName("findPreviousChapter()")
  class FindPreviousChapterTests {

    @Test
    @DisplayName("should return previous chapter")
    void shouldReturnPreviousChapter() {
      Chapter previousChapter = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      testChapter.setNumberFormatted("0002");
      when(repository.findPreviousChapter(
          workId, "0002", "0000", PageRequest.of(0, 1)))
          .thenReturn(List.of(previousChapter));

      Optional<Chapter> result = chapterService.findPreviousChapter(testChapter);

      assertTrue(result.isPresent());
      assertEquals(previousChapter, result.get());
    }

    @Test
    @DisplayName("should return empty when no previous chapter exists")
    void shouldReturnEmptyWhenNoPreviousChapter() {
      when(repository.findPreviousChapter(
          workId, "0001", "0000", PageRequest.of(0, 1)))
          .thenReturn(Collections.emptyList());

      Optional<Chapter> result = chapterService.findPreviousChapter(testChapter);

      assertFalse(result.isPresent());
    }
  }

  @Nested
  @DisplayName("findNextChapter()")
  class FindNextChapterTests {

    @Test
    @DisplayName("should return next chapter")
    void shouldReturnNextChapter() {
      Chapter nextChapter = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(repository.findNextChapter(
          workId, "0001", "0000", PageRequest.of(0, 1)))
          .thenReturn(List.of(nextChapter));

      Optional<Chapter> result = chapterService.findNextChapter(testChapter);

      assertTrue(result.isPresent());
      assertEquals(nextChapter, result.get());
    }

    @Test
    @DisplayName("should return empty when no next chapter exists")
    void shouldReturnEmptyWhenNoNextChapter() {
      when(repository.findNextChapter(
          workId, "0001", "0000", PageRequest.of(0, 1)))
          .thenReturn(Collections.emptyList());

      Optional<Chapter> result = chapterService.findNextChapter(testChapter);

      assertFalse(result.isPresent());
    }
  }

  @Nested
  @DisplayName("findFirstUnreadChapter()")
  class FindFirstUnreadChapterTests {

    @Test
    @DisplayName("should return first unread chapter")
    void shouldReturnFirstUnreadChapter() {
      UUID userId = UUID.randomUUID();
      when(repository.findFirstUnreadChapter(workId, userId, PageRequest.of(0, 1)))
          .thenReturn(List.of(testChapter));

      Optional<Chapter> result = chapterService.findFirstUnreadChapter(workId, userId);

      assertTrue(result.isPresent());
      assertEquals(testChapter, result.get());
    }

    @Test
    @DisplayName("should return empty when all chapters are read")
    void shouldReturnEmptyWhenAllChaptersAreRead() {
      UUID userId = UUID.randomUUID();
      when(repository.findFirstUnreadChapter(workId, userId, PageRequest.of(0, 1)))
          .thenReturn(Collections.emptyList());

      Optional<Chapter> result = chapterService.findFirstUnreadChapter(workId, userId);

      assertFalse(result.isPresent());
    }
  }

  @Nested
  @DisplayName("findPagedByWorkSlug()")
  class FindPagedByWorkSlugTests {

    @Test
    @DisplayName("should return paged chapters with descending sort")
    void shouldReturnPagedChaptersWithDescendingSort() {
      Page<Chapter> expectedPage = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 50), 1);
      when(repository.findPagedByWorkSlug(eq("test-slug"), eq(null), any(Pageable.class)))
          .thenReturn(expectedPage);

      Page<Chapter> result = chapterService.findPagedByWorkSlug("test-slug", 0, 50, "desc", null);

      assertEquals(1, result.getTotalElements());
      assertEquals(0, result.getNumber());
      assertEquals(50, result.getSize());
    }

    @Test
    @DisplayName("should return paged chapters with ascending sort")
    void shouldReturnPagedChaptersWithAscendingSort() {
      Page<Chapter> expectedPage = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 50), 1);
      when(repository.findPagedByWorkSlug(eq("test-slug"), eq(null), any(Pageable.class)))
          .thenReturn(expectedPage);

      Page<Chapter> result = chapterService.findPagedByWorkSlug("test-slug", 0, 50, "asc", null);

      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("should filter by language when provided")
    void shouldFilterByLanguageWhenProvided() {
      Page<Chapter> expectedPage = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 50), 1);
      when(repository.findPagedByWorkSlug(eq("test-slug"), eq("EN"), any(Pageable.class)))
          .thenReturn(expectedPage);

      Page<Chapter> result = chapterService.findPagedByWorkSlug("test-slug", 0, 50, "desc", "EN");

      assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("should use correct pagination parameters")
    void shouldUseCorrectPaginationParameters() {
      Page<Chapter> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(2, 25), 0);
      when(repository.findPagedByWorkSlug(eq("test-slug"), eq(null), any(Pageable.class)))
          .thenReturn(expectedPage);

      Page<Chapter> result = chapterService.findPagedByWorkSlug("test-slug", 2, 25, "desc", null);

      assertEquals(2, result.getNumber());
      assertEquals(25, result.getSize());
    }
  }
}
