package dev.williancorrea.manhwa.reader.features.chapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterListOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterPageOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterReaderOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.NextUnreadOutput;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.library.LibraryService;
import dev.williancorrea.manhwa.reader.features.library.LibraryStatus;
import dev.williancorrea.manhwa.reader.features.page.Page;
import dev.williancorrea.manhwa.reader.features.page.PageService;
import dev.williancorrea.manhwa.reader.features.page.PageType;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgress;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgressService;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.storage.StorageInterface;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChapterResource")
class ChapterResourceTest {

  @Mock
  private ChapterService chapterService;

  @Mock
  private ReadingProgressService readingProgressService;

  @Mock
  private LibraryService libraryService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private WorkService workService;

  @Mock
  private PageService pageService;

  @Mock
  private StorageInterface storageService;

  @InjectMocks
  private ChapterResource chapterResource;

  private UUID userId;
  private UUID workId;
  private UUID chapterId;
  private String slug;
  private User testUser;
  private Work testWork;
  private Chapter testChapter;
  private Language testLanguage;
  private Scanlator testScanlator;
  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    workId = UUID.randomUUID();
    chapterId = UUID.randomUUID();
    slug = "test-work-slug";

    testLanguage = Language.builder()
        .id(UUID.randomUUID())
        .code("EN")
        .build();
    testScanlator = Scanlator.builder()
        .id(UUID.randomUUID())
        .name("TestScanlator")
        .code("TS")
        .build();
    testWork = Work.builder()
        .id(workId)
        .slug(slug)
        .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
        .build();
    testChapter = Chapter.builder()
        .id(chapterId)
        .work(testWork)
        .number("1")
        .numberFormatted("0001")
        .numberVersion("0000")
        .title("Chapter Title")
        .language(testLanguage)
        .scanlator(testScanlator)
        .releaseDate(LocalDate.now())
        .synced(true)
        .build();
    testUser = User.builder()
        .id(userId)
        .email("test@example.com")
        .name("Test User")
        .build();
    userDetails = mock(UserDetails.class, withSettings().lenient());
    when(userDetails.getUsername()).thenReturn("test@example.com");
  }

  @Nested
  @DisplayName("getNextUnread()")
  class GetNextUnreadTests {

    @Test
    @DisplayName("should return next unread chapter when found")
    void shouldReturnNextUnreadChapterWhenFound() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(workService.findBySlug(slug)).thenReturn(Optional.of(testWork));
      when(chapterService.findFirstUnreadChapter(workId, userId)).thenReturn(Optional.of(testChapter));
      when(readingProgressService.hasReadAnyChapter(testUser, workId)).thenReturn(true);

      ResponseEntity<NextUnreadOutput> response = chapterResource.getNextUnread(slug, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().id()).isEqualTo(chapterId);
      assertThat(response.getBody().numberWithVersion()).isEqualTo("1");
      assertThat(response.getBody().hasReadChapters()).isTrue();
    }

    @Test
    @DisplayName("should return null chapter id when no unread chapter exists")
    void shouldReturnNullChapterIdWhenNoUnreadChapterExists() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(workService.findBySlug(slug)).thenReturn(Optional.of(testWork));
      when(chapterService.findFirstUnreadChapter(workId, userId)).thenReturn(Optional.empty());
      when(readingProgressService.hasReadAnyChapter(testUser, workId)).thenReturn(false);

      ResponseEntity<NextUnreadOutput> response = chapterResource.getNextUnread(slug, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().id()).isNull();
      assertThat(response.getBody().numberWithVersion()).isNull();
      assertThat(response.getBody().hasReadChapters()).isFalse();
    }
  }

  @Nested
  @DisplayName("findChapters()")
  class FindChaptersTests {

    @Test
    @DisplayName("should return list of chapters with default parameters")
    void shouldReturnListOfChaptersWithDefaultParameters() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      var page = new PageImpl<>(List.of(testChapter, chapter2), PageRequest.of(0, 50), 2);
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findPagedByWorkSlug(slug, 0, 50, "desc", null)).thenReturn(page);
      when(readingProgressService.findAllByUserAndChapterIds(testUser, List.of(chapterId, chapter2.getId())))
          .thenReturn(Map.of());

      ResponseEntity<org.springframework.data.domain.Page<ChapterListOutput>> response = chapterResource.findChapters(
          slug, 0, 50, "desc", null, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("should limit page size to maximum 100")
    void shouldLimitPageSizeToMaximum100() {
      var page = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 100), 1);
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findPagedByWorkSlug(slug, 0, 100, "desc", null)).thenReturn(page);
      when(readingProgressService.findAllByUserAndChapterIds(testUser, List.of(chapterId)))
          .thenReturn(Map.of());

      ResponseEntity<org.springframework.data.domain.Page<ChapterListOutput>> response = chapterResource.findChapters(
          slug, 0, 200, "desc", null, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(chapterService).findPagedByWorkSlug(slug, 0, 100, "desc", null);
    }

    @Test
    @DisplayName("should filter by language when provided")
    void shouldFilterByLanguageWhenProvided() {
      var page = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 50), 1);
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findPagedByWorkSlug(slug, 0, 50, "desc", "EN")).thenReturn(page);
      when(readingProgressService.findAllByUserAndChapterIds(testUser, List.of(chapterId)))
          .thenReturn(Map.of());

      ResponseEntity<org.springframework.data.domain.Page<ChapterListOutput>> response = chapterResource.findChapters(
          slug, 0, 50, "desc", "EN", userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(chapterService).findPagedByWorkSlug(slug, 0, 50, "desc", "EN");
    }

    @Test
    @DisplayName("should include reading progress in output")
    void shouldIncludeReadingProgressInOutput() {
      ReadingProgress progress = ReadingProgress.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .user(testUser)
          .pageNumber(5)
          .build();
      var page = new PageImpl<>(List.of(testChapter), PageRequest.of(0, 50), 1);
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findPagedByWorkSlug(slug, 0, 50, "desc", null)).thenReturn(page);
      when(readingProgressService.findAllByUserAndChapterIds(testUser, List.of(chapterId)))
          .thenReturn(Map.of(chapterId, progress));

      ResponseEntity<org.springframework.data.domain.Page<ChapterListOutput>> response = chapterResource.findChapters(
          slug, 0, 50, "desc", null, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().getContent()).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("markChapterRead()")
  class MarkChapterReadTests {

    @Test
    @DisplayName("should mark chapter and previous chapters as read")
    void shouldMarkChapterAndPreviousChaptersAsRead() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(libraryService.findByUserAndWork(testUser, testWork)).thenReturn(Optional.empty());
      when(chapterService.findChaptersUpTo(workId, "0001", "0000")).thenReturn(List.of(testChapter));

      ResponseEntity<Void> response = chapterResource.markChapterRead(slug, chapterId, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(libraryService).saveOrUpdate(testUser, testWork, LibraryStatus.READING);
      verify(readingProgressService).markAllAsRead(testUser, List.of(testChapter));
    }

    @Test
    @DisplayName("should not add to library if already exists")
    void shouldNotAddToLibraryIfAlreadyExists() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(libraryService.findByUserAndWork(testUser, testWork)).thenReturn(Optional.of(mock()));
      when(chapterService.findChaptersUpTo(workId, "0001", "0000")).thenReturn(List.of(testChapter));

      chapterResource.markChapterRead(slug, chapterId, userDetails);

      verify(libraryService, times(0)).saveOrUpdate(any(), any(), any());
    }
  }

  @Nested
  @DisplayName("markChapterUnread()")
  class MarkChapterUnreadTests {

    @Test
    @DisplayName("should unmark chapter and subsequent chapters")
    void shouldUnmarkChapterAndSubsequentChapters() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(chapterService.findChaptersFrom(workId, "0001", "0000")).thenReturn(List.of(testChapter, chapter2));

      ResponseEntity<Void> response = chapterResource.markChapterUnread(slug, chapterId, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      verify(readingProgressService).deleteByUserAndChapter(testUser, testChapter);
      verify(readingProgressService).deleteByUserAndChapter(testUser, chapter2);
    }
  }

  @Nested
  @DisplayName("markAllChaptersRead()")
  class MarkAllChaptersReadTests {

    @Test
    @DisplayName("should mark all chapters as read")
    void shouldMarkAllChaptersAsRead() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(workService.findBySlug(slug)).thenReturn(Optional.of(testWork));
      when(chapterService.findAllByWorkId(workId)).thenReturn(List.of(testChapter, chapter2));

      ResponseEntity<Void> response = chapterResource.markAllChaptersRead(slug, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(readingProgressService).markAllAsRead(testUser, List.of(testChapter, chapter2));
    }

    @Test
    @DisplayName("should handle empty chapter list")
    void shouldHandleEmptyChapterList() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(workService.findBySlug(slug)).thenReturn(Optional.of(testWork));
      when(chapterService.findAllByWorkId(workId)).thenReturn(Collections.emptyList());

      ResponseEntity<Void> response = chapterResource.markAllChaptersRead(slug, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(readingProgressService).markAllAsRead(testUser, Collections.emptyList());
    }
  }

  @Nested
  @DisplayName("markAllChaptersUnread()")
  class MarkAllChaptersUnreadTests {

    @Test
    @DisplayName("should unmark all chapters")
    void shouldUnmarkAllChapters() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
      when(workService.findBySlug(slug)).thenReturn(Optional.of(testWork));

      ResponseEntity<Void> response = chapterResource.markAllChaptersUnread(slug, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      verify(readingProgressService).unmarkAllByWorkId(testUser, workId);
    }
  }

  @Nested
  @DisplayName("getChapterForReader()")
  class GetChapterForReaderTests {

    @Test
    @DisplayName("should return chapter reader output with pages")
    void shouldReturnChapterReaderOutputWithPages() {
      Page page = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page1.jpg")
          .type(PageType.IMAGE)
          .build();
      testWork.setTitles(List.of(WorkTitle.builder().title("Official Title").isOfficial(true).build()));
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(pageService.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(page));
      when(storageService.findObjectByNamePresigned(anyString()))
          .thenReturn("https://storage.example.com/image.jpg");

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().id()).isEqualTo(chapterId);
      assertThat(response.getBody().workSlug()).isEqualTo(slug);
      assertThat(response.getBody().pages()).hasSize(1);
    }

    @Test
    @DisplayName("should return not found when slug does not match")
    void shouldReturnNotFoundWhenSlugDoesNotMatch() {
      Chapter chapterWithDifferentSlug = Chapter.builder()
          .id(chapterId)
          .work(Work.builder().id(workId).slug("different-slug").build())
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(chapterWithDifferentSlug));

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("should use first title when no official title exists")
    void shouldUseFirstTitleWhenNoOfficialTitleExists() {
      testWork.setTitles(List.of(
          WorkTitle.builder().title("Alternative Title").isOfficial(false).build(),
          WorkTitle.builder().title("Other Title").isOfficial(false).build()
      ));
      Page page = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page1.jpg")
          .type(PageType.IMAGE)
          .build();
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(pageService.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(page));
      when(storageService.findObjectByNamePresigned(anyString()))
          .thenReturn("https://storage.example.com/image.jpg");

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().workTitle()).isEqualTo("Alternative Title");
    }

    @Test
    @DisplayName("should handle missing work titles")
    void shouldHandleMissingWorkTitles() {
      testWork.setTitles(null);
      Page page = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page1.jpg")
          .type(PageType.IMAGE)
          .build();
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(pageService.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(page));
      when(storageService.findObjectByNamePresigned(anyString()))
          .thenReturn("https://storage.example.com/image.jpg");

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().workTitle()).isNull();
    }

    @Test
    @DisplayName("should build correct storage path for image pages")
    void shouldBuildCorrectStoragePathForImagePages() {
      Page page = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page1.jpg")
          .type(PageType.IMAGE)
          .build();
      testWork.setTitles(Collections.emptyList());
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapter));
      when(pageService.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(page));
      when(storageService.findObjectByNamePresigned(anyString()))
          .thenReturn("https://storage.example.com/image.jpg");

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().pages().getFirst().imageUrl()).isNotNull();
    }

    @Test
    @DisplayName("should include text content for text pages")
    void shouldIncludeTextContentForTextPages() {
      Page textPage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .type(PageType.MARKDOWN)
          .content("Chapter content")
          .build();
      testWork.setTitles(Collections.emptyList());
      Chapter testChapterLocal = Chapter.builder()
          .id(chapterId)
          .work(testWork)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .title("Chapter Title")
          .language(testLanguage)
          .scanlator(testScanlator)
          .build();
      when(chapterService.findById(chapterId)).thenReturn(Optional.of(testChapterLocal));
      when(pageService.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(textPage));

      ResponseEntity<ChapterReaderOutput> response = chapterResource.getChapterForReader(slug, chapterId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().pages().getFirst().content()).isEqualTo("Chapter content");
      assertThat(response.getBody().pages().getFirst().imageUrl()).isNull();
    }
  }
}
