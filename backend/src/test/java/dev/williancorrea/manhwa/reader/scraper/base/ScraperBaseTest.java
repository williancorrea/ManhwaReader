package dev.williancorrea.manhwa.reader.scraper.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorService;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.scanlator.ScanlatorService;
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationError;
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationErrorService;
import dev.williancorrea.manhwa.reader.features.tag.TagService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkContentRating;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.WorkTag;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.cover.WorkCover;
import dev.williancorrea.manhwa.reader.features.work.link.Site;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLinkRepository;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationLinks;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTags;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.storage.ExternalFileService;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScraperBase")
class ScraperBaseTest {

  @Mock
  private WorkService workService;

  @Mock
  private LanguageService languageService;

  @Mock
  private ScanlatorSynchronizationErrorService scanlatorSynchronizationErrorService;

  @Mock
  private ScanlatorService scanlatorService;

  @Mock
  private WorkLinkRepository workLinkRepository;

  @Mock
  private TagService tagService;

  @Mock
  private AuthorService authorService;

  @Mock
  private ExternalFileService externalFileService;

  @Mock
  private dev.williancorrea.manhwa.reader.email.EmailService emailService;

  private ScraperBase scraperBase;

  @BeforeEach
  void setUp() {
    scraperBase = new ScraperBase(
        workService,
        languageService,
        scanlatorSynchronizationErrorService,
        scanlatorService,
        workLinkRepository,
        tagService,
        authorService,
        externalFileService,
        emailService
    );
  }

  @Nested
  @DisplayName("findWorkOrCreate")
  class FindWorkOrCreateTests {

    @Test
    void shouldFindExistingWork() {
      var externalId = "123";
      var origin = SynchronizationOriginType.MEDIOCRESCAN;
      var existingWork = Work.builder().id(java.util.UUID.randomUUID()).build();

      when(workService.findBySynchronizationExternalID(externalId, origin))
          .thenReturn(Optional.of(existingWork));

      var result = scraperBase.findWorkOrCreate(externalId, origin);

      assertEquals(existingWork, result);
      verify(workService).findBySynchronizationExternalID(externalId, origin);
    }

    @Test
    void shouldCreateNewWorkWhenNotFound() {
      var externalId = "456";
      var origin = SynchronizationOriginType.MANGADEX;

      when(workService.findBySynchronizationExternalID(externalId, origin))
          .thenReturn(Optional.empty());

      var result = scraperBase.findWorkOrCreate(externalId, origin);

      assertNotNull(result);
      assertFalse(result.getDisabled());
      assertNotNull(result.getCreatedAt());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenExternalIdIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.findWorkOrCreate(null, SynchronizationOriginType.MEDIOCRESCAN));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenOriginIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.findWorkOrCreate("123", null));
    }
  }

  @Nested
  @DisplayName("syncTitle")
  class SyncTitleTests {

    private Work work;
    private Language language;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setTitles(new ArrayList<>());
      language = Language.builder().code("pt-BR").build();
    }

    @Test
    void shouldSyncNewTitle() {
      var title = SynchronizationTitle.builder()
          .title("Test Manhwa")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncTitle(work, List.of(title));

      assertEquals(1, work.getTitles().size());
      assertEquals("Test Manhwa", work.getTitles().get(0).getTitle());
      assertTrue(work.getTitles().get(0).getIsOfficial());
    }

    @Test
    void shouldSkipDuplicateTitle() {
      var existingTitle = WorkTitle.builder()
          .title("Test Manhwa")
          .language(language)
          .work(work)
          .isOfficial(false)
          .build();
      work.getTitles().add(existingTitle);

      var title = SynchronizationTitle.builder()
          .title("Test Manhwa")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncTitle(work, List.of(title));

      assertEquals(1, work.getTitles().size());
    }

    @Test
    void shouldSkipBracketTitle() {
      var title = SynchronizationTitle.builder()
          .title("[]")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      scraperBase.syncTitle(work, List.of(title));

      assertEquals(0, work.getTitles().size());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      var title = SynchronizationTitle.builder()
          .title("Test")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      assertThrows(NullPointerException.class, () -> scraperBase.syncTitle(null, List.of(title)));
    }

    @Test
    void shouldThrowExceptionWhenTitlesIsNull() {
      assertThrows(NullPointerException.class, () -> scraperBase.syncTitle(work, null));
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
      var title = SynchronizationTitle.builder()
          .title("   ")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      assertThrows(IllegalArgumentException.class, () -> scraperBase.syncTitle(work, List.of(title)));
    }

    @Test
    void shouldUpdateLanguageForExistingTitle() {
      var unknownLanguage = Language.builder().code("xx-XX").build();
      var existingTitle = WorkTitle.builder()
          .title("Test Manhwa")
          .language(unknownLanguage)
          .work(work)
          .isOfficial(false)
          .build();
      work.getTitles().add(existingTitle);

      var title = SynchronizationTitle.builder()
          .title("Test Manhwa")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncTitle(work, List.of(title));

      assertEquals(language, existingTitle.getLanguage());
    }

    @Test
    void shouldInitializeTitlesListWhenNull() {
      work.setTitles(null);
      var title = SynchronizationTitle.builder()
          .title("Test")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncTitle(work, List.of(title));

      assertNotNull(work.getTitles());
      assertEquals(1, work.getTitles().size());
    }
  }

  @Nested
  @DisplayName("syncSynopses")
  class SyncSynopsesTests {

    private Work work;
    private Language language;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setSynopses(new ArrayList<>());
      language = Language.builder().code("pt-BR").build();
    }

    @Test
    void shouldSyncNewSynopsis() {
      var synopsis = SynchronizationSynopses.builder()
          .description("Test synopsis")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncSynopses(work, List.of(synopsis));

      assertEquals(1, work.getSynopses().size());
      assertEquals("Test synopsis", work.getSynopses().get(0).getDescription());
    }

    @Test
    void shouldSkipBlankSynopsis() {
      var synopsis = SynchronizationSynopses.builder()
          .description("   ")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      scraperBase.syncSynopses(work, List.of(synopsis));

      assertEquals(0, work.getSynopses().size());
    }

    @Test
    void shouldSkipDuplicateSynopsis() {
      var existingSynopsis = WorkSynopsis.builder()
          .description("Test synopsis")
          .language(language)
          .work(work)
          .build();
      work.getSynopses().add(existingSynopsis);

      var synopsis = SynchronizationSynopses.builder()
          .description("New synopsis")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      scraperBase.syncSynopses(work, List.of(synopsis));

      assertEquals(1, work.getSynopses().size());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      var synopsis = SynchronizationSynopses.builder()
          .description("Test")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      assertThrows(NullPointerException.class,
          () -> scraperBase.syncSynopses(null, List.of(synopsis)));
    }

    @Test
    void shouldThrowExceptionWhenSynopsesIsNull() {
      assertThrows(NullPointerException.class, () -> scraperBase.syncSynopses(work, null));
    }

    @Test
    void shouldInitializeSynopsesListWhenNull() {
      work.setSynopses(null);
      var synopsis = SynchronizationSynopses.builder()
          .description("Test")
          .language("pt-BR")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(languageService.findOrCreate("pt-BR", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(language);

      scraperBase.syncSynopses(work, List.of(synopsis));

      assertNotNull(work.getSynopses());
      assertEquals(1, work.getSynopses().size());
    }
  }

  @Nested
  @DisplayName("syncWorkError (overloaded methods)")
  class SyncWorkErrorTests {

    @Test
    void shouldSyncWorkErrorWithoutUrl() {
      var origin = SynchronizationOriginType.MEDIOCRESCAN;
      var scanlator = Scanlator.builder().name("Test Scanlator").build();

      when(scanlatorService.findBySynchronization(origin))
          .thenReturn(Optional.of(scanlator));
      doNothing().when(emailService).sendScraperErrorEmail(anyString(), anyString(), any());

      scraperBase.syncWorkError(origin, "1", "ext123", "Test Work", "Error message", "stacktrace");

      verify(scanlatorSynchronizationErrorService).save(any(ScanlatorSynchronizationError.class));
      verify(emailService).sendScraperErrorEmail(anyString(), eq("Error message"), any());
    }

    @Test
    void shouldSyncWorkErrorWithUrl() {
      var origin = SynchronizationOriginType.MANGADEX;
      var scanlator = Scanlator.builder().name("Test Scanlator").build();

      when(scanlatorService.findBySynchronization(origin))
          .thenReturn(Optional.of(scanlator));
      doNothing().when(emailService).sendScraperErrorEmail(anyString(), anyString(), any());

      scraperBase.syncWorkError(origin, "1", "ext123", "Test Work", "Error message", "stacktrace", "http://example.com");

      verify(scanlatorSynchronizationErrorService).save(any(ScanlatorSynchronizationError.class));
    }

    @Test
    void shouldThrowExceptionWhenOriginIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncWorkError(null, "1", "ext123", "Work", "Error", "trace"));
    }

    @Test
    void shouldThrowExceptionWhenExternalIdIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncWorkError(SynchronizationOriginType.MEDIOCRESCAN, "1", null, "Work", "Error", "trace"));
    }

    @Test
    void shouldThrowExceptionWhenExternalNameIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncWorkError(SynchronizationOriginType.MEDIOCRESCAN, "1", "ext123", null, "Error", "trace"));
    }

    @Test
    void shouldThrowExceptionWhenErrorMessageIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncWorkError(SynchronizationOriginType.MEDIOCRESCAN, "1", "ext123", "Work", null, "trace"));
    }

    @Test
    void shouldHandleEmailServiceException() {
      var origin = SynchronizationOriginType.MEDIOCRESCAN;

      when(scanlatorService.findBySynchronization(origin))
          .thenReturn(Optional.empty());
      doThrow(new RuntimeException("Email service failed"))
          .when(emailService).sendScraperErrorEmail(anyString(), anyString(), any());

      scraperBase.syncWorkError(origin, "1", "ext123", "Test Work", "Error message", "stacktrace");

      verify(scanlatorSynchronizationErrorService).save(any(ScanlatorSynchronizationError.class));
    }
  }

  @Nested
  @DisplayName("syncAttributes")
  class SyncAttributesTests {

    private Work work;
    private Language language;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      language = Language.builder().code("ko").build();
    }

    @Test
    void shouldSetWorkTypeWhenNull() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null);

      assertEquals(WorkType.MANHWA, work.getType());
    }

    @Test
    void shouldNotOverrideExistingWorkType() {
      work.setType(WorkType.MANGA);

      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null);

      assertEquals(WorkType.MANGA, work.getType());
    }

    @Test
    void shouldSetPublicationDemographicWhenNull() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null);

      assertEquals(WorkPublicationDemographic.SHOUNEN, work.getPublicationDemographic());
    }

    @Test
    void shouldNotOverridePublicationDemographicWhenNotUnknown() {
      work.setPublicationDemographic(WorkPublicationDemographic.SHOUJO);

      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null);

      assertEquals(WorkPublicationDemographic.SHOUJO, work.getPublicationDemographic());
    }

    @Test
    void shouldDetectWorkTypeFromLanguage() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANGA,
          WorkStatus.ONGOING, "test-slug", false, 2020, language, null, null);

      assertEquals(WorkType.MANHWA, work.getType());
    }

    @Test
    void shouldNotOverrideWorkTypeWhenNovel() {
      work.setType(WorkType.NOVEL);

      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANGA,
          WorkStatus.ONGOING, "test-slug", false, 2020, language, null, null);

      assertEquals(WorkType.NOVEL, work.getType());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncAttributes(null, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
              WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null));
    }

    @Test
    void shouldThrowExceptionWhenSlugIsBlank() {
      assertThrows(IllegalArgumentException.class,
          () -> scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
              WorkStatus.ONGOING, "   ", false, 2020, null, null, null));
    }

    @Test
    void shouldSetSlugWhenUniqueAndFree() {
      when(workService.findBySlug(anyString())).thenReturn(Optional.empty());

      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "Test Slug", false, 2020, null, null, null);

      assertNotNull(work.getSlug());
      assertTrue(work.getSlug().length() > 0);
    }

    @Test
    void shouldGenerateSlugWhenDuplicate() {
      var existingWork = Work.builder().build();
      when(workService.findBySlug(anyString())).thenReturn(Optional.of(existingWork));

      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "Test Slug", false, 2020, null, null, null);

      assertTrue(work.getSlug().contains("__generated_"));
    }

    @Test
    void shouldSetReleaseYear() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, null);

      assertEquals(2020, work.getReleaseYear());
    }

    @Test
    void shouldSetContentRating() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, WorkContentRating.SAFE, null);

      assertEquals(WorkContentRating.SAFE, work.getContentRating());
    }

    @Test
    void shouldSetChapterNumbersReset() {
      scraperBase.syncAttributes(work, WorkPublicationDemographic.SHOUNEN, WorkType.MANHWA,
          WorkStatus.ONGOING, "test-slug", false, 2020, null, null, true);

      assertTrue(work.getChapterNumbersResetOnNewVolume());
    }
  }

  @Nested
  @DisplayName("syncSynchronization")
  class SyncSynchronizationTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setSynchronizations(new ArrayList<>());
    }

    @Test
    void shouldAddSynchronization() {
      scraperBase.syncSynchronization(work, "ext123", SynchronizationOriginType.MEDIOCRESCAN, "test-slug");

      assertEquals(1, work.getSynchronizations().size());
      assertEquals("ext123", work.getSynchronizations().get(0).getExternalId());
    }

    @Test
    void shouldNotAddDuplicateSynchronization() {
      var sync = WorkSynchronization.builder()
          .externalId("ext123")
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .work(work)
          .build();
      work.getSynchronizations().add(sync);

      scraperBase.syncSynchronization(work, "ext123", SynchronizationOriginType.MEDIOCRESCAN, "test-slug");

      assertEquals(1, work.getSynchronizations().size());
    }

    @Test
    void shouldInitializeSynchronizationsListWhenNull() {
      work.setSynchronizations(null);

      scraperBase.syncSynchronization(work, "ext123", SynchronizationOriginType.MEDIOCRESCAN, "test-slug");

      assertNotNull(work.getSynchronizations());
      assertEquals(1, work.getSynchronizations().size());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncSynchronization(null, "ext123", SynchronizationOriginType.MEDIOCRESCAN, "test-slug"));
    }

    @Test
    void shouldThrowExceptionWhenExternalIdIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncSynchronization(work, null, SynchronizationOriginType.MEDIOCRESCAN, "test-slug"));
    }

    @Test
    void shouldThrowExceptionWhenOriginIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncSynchronization(work, "ext123", null, "test-slug"));
    }
  }

  @Nested
  @DisplayName("isWorkUpdated")
  class IsWorkUpdatedTests {

    private Work work;
    private Scanlator scanlator;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setSynchronizations(new ArrayList<>());
      work.setChapters(new ArrayList<>());
      scanlator = Scanlator.builder().build();
    }

    @Test
    void shouldReturnFalseWhenSynchronizationsNull() {
      work.setSynchronizations(null);

      boolean result = scraperBase.isWorkUpdated(work, SynchronizationOriginType.MEDIOCRESCAN,
          OffsetDateTime.now());

      assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenWorkIsUpdated() {
      var updateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      var sync = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .updatedWorkAt(updateTime)
          .build();
      work.getSynchronizations().add(sync);

      when(scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(Optional.of(scanlator));

      boolean result = scraperBase.isWorkUpdated(work, SynchronizationOriginType.MEDIOCRESCAN, updateTime);

      assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNoMatchingSynchronization() {
      when(scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(Optional.of(scanlator));

      boolean result = scraperBase.isWorkUpdated(work, SynchronizationOriginType.MEDIOCRESCAN,
          OffsetDateTime.now());

      assertFalse(result);
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.isWorkUpdated(null, SynchronizationOriginType.MEDIOCRESCAN, OffsetDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenOriginIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.isWorkUpdated(work, null, OffsetDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenUpdateAtIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.isWorkUpdated(work, SynchronizationOriginType.MEDIOCRESCAN, null));
    }
  }

  @Nested
  @DisplayName("updatingSyncWorkTime")
  class UpdatingSyncWorkTimeTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setSynchronizations(new ArrayList<>());
      var sync = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build();
      work.getSynchronizations().add(sync);
    }

    @Test
    void shouldUpdateSyncTime() {
      var createTime = OffsetDateTime.now();
      var updateTime = OffsetDateTime.now().plusHours(1);

      scraperBase.updatingSyncWorkTime(work, SynchronizationOriginType.MEDIOCRESCAN, createTime, updateTime);

      var sync = work.getSynchronizations().get(0);
      assertEquals(createTime.truncatedTo(ChronoUnit.SECONDS), sync.getCreatedWorkAt());
      assertEquals(updateTime.truncatedTo(ChronoUnit.SECONDS), sync.getUpdatedWorkAt());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.updatingSyncWorkTime(null, SynchronizationOriginType.MEDIOCRESCAN, OffsetDateTime.now(), OffsetDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenCreateAtIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.updatingSyncWorkTime(work, SynchronizationOriginType.MEDIOCRESCAN, null, OffsetDateTime.now()));
    }

    @Test
    void shouldThrowExceptionWhenUpdateAtIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.updatingSyncWorkTime(work, SynchronizationOriginType.MEDIOCRESCAN, OffsetDateTime.now(), null));
    }
  }

  @Nested
  @DisplayName("syncLinks")
  class SyncLinksTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setLinks(new ArrayList<>());
    }

    @Test
    void shouldSyncNewLink() {
      var site = Site.builder().code(SiteType.MANGADEX.name()).build();
      var link = SynchronizationLinks.builder()
          .siteType(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      when(workLinkRepository.findBySiteCode(SiteType.MANGADEX.name()))
          .thenReturn(Optional.of(site));

      scraperBase.syncLinks(work, List.of(link));

      assertEquals(1, work.getLinks().size());
    }

    @Test
    void shouldSkipLinkWithNullSiteType() {
      var link = SynchronizationLinks.builder()
          .siteType(null)
          .link("https://example.com")
          .build();

      scraperBase.syncLinks(work, List.of(link));

      assertEquals(0, work.getLinks().size());
    }

    @Test
    void shouldSkipLinkWithNullLink() {
      var link = SynchronizationLinks.builder()
          .siteType(SiteType.MANGADEX)
          .link(null)
          .build();

      scraperBase.syncLinks(work, List.of(link));

      assertEquals(0, work.getLinks().size());
    }

    @Test
    void shouldThrowExceptionWhenLinkIsBlank() {
      var link = SynchronizationLinks.builder()
          .siteType(SiteType.MANGADEX)
          .link("   ")
          .build();

      assertThrows(IllegalArgumentException.class, () -> scraperBase.syncLinks(work, List.of(link)));
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      var link = SynchronizationLinks.builder()
          .siteType(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      assertThrows(NullPointerException.class, () -> scraperBase.syncLinks(null, List.of(link)));
    }

    @Test
    void shouldThrowExceptionWhenLinksIsNull() {
      assertThrows(NullPointerException.class, () -> scraperBase.syncLinks(work, null));
    }

    @Test
    void shouldInitializeLinksListWhenNull() {
      work.setLinks(null);
      var site = Site.builder().code(SiteType.MANGADEX.name()).build();
      var link = SynchronizationLinks.builder()
          .siteType(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      when(workLinkRepository.findBySiteCode(SiteType.MANGADEX.name()))
          .thenReturn(Optional.of(site));

      scraperBase.syncLinks(work, List.of(link));

      assertNotNull(work.getLinks());
      assertEquals(1, work.getLinks().size());
    }
  }

  @Nested
  @DisplayName("syncTags")
  class SyncTagsTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setTags(new ArrayList<>());
    }

    @Test
    void shouldSyncNewTag() {
      var mockTag = mock(dev.williancorrea.manhwa.reader.features.tag.Tag.class);
      var tag = SynchronizationTags.builder()
          .group(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE)
          .name("action")
          .build();

      when(tagService.findOrCreate(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE, "action"))
          .thenReturn(mockTag);

      scraperBase.syncTags(work, List.of(tag));

      assertEquals(1, work.getTags().size());
    }

    @Test
    void shouldSkipTagWithNullGroup() {
      var tag = SynchronizationTags.builder()
          .group(null)
          .name("action")
          .build();

      scraperBase.syncTags(work, List.of(tag));

      assertEquals(0, work.getTags().size());
    }

    @Test
    void shouldSkipTagWithNullName() {
      var tag = SynchronizationTags.builder()
          .group(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE)
          .name(null)
          .build();

      scraperBase.syncTags(work, List.of(tag));

      assertEquals(0, work.getTags().size());
    }

    @Test
    void shouldSkipTagWithBlankName() {
      var tag = SynchronizationTags.builder()
          .group(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE)
          .name("   ")
          .build();

      scraperBase.syncTags(work, List.of(tag));

      assertEquals(0, work.getTags().size());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      var tag = SynchronizationTags.builder()
          .group(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE)
          .name("action")
          .build();

      assertThrows(NullPointerException.class, () -> scraperBase.syncTags(null, List.of(tag)));
    }

    @Test
    void shouldThrowExceptionWhenTagsIsNull() {
      assertThrows(NullPointerException.class, () -> scraperBase.syncTags(work, null));
    }

    @Test
    void shouldInitializeTagsListWhenNull() {
      work.setTags(null);
      var mockTag = mock(dev.williancorrea.manhwa.reader.features.tag.Tag.class);
      var tag = SynchronizationTags.builder()
          .group(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE)
          .name("action")
          .build();

      when(tagService.findOrCreate(dev.williancorrea.manhwa.reader.features.tag.TagGroupType.GENRE, "action"))
          .thenReturn(mockTag);

      scraperBase.syncTags(work, List.of(tag));

      assertNotNull(work.getTags());
      assertEquals(1, work.getTags().size());
    }
  }

  @Nested
  @DisplayName("syncAuthors")
  class SyncAuthorsTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setAuthors(new ArrayList<>());
    }

    @Test
    void shouldSyncNewAuthor() {
      var author = Author.builder()
          .name("Test Author")
          .type(AuthorType.AUTHOR)
          .build();

      when(authorService.findOrCreate(author))
          .thenReturn(author);

      scraperBase.syncAuthors(work, List.of(author));

      assertEquals(1, work.getAuthors().size());
    }

    @Test
    void shouldSkipAuthorWithBlankName() {
      var author = Author.builder()
          .name("   ")
          .type(AuthorType.AUTHOR)
          .build();

      scraperBase.syncAuthors(work, List.of(author));

      assertEquals(0, work.getAuthors().size());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      var author = Author.builder()
          .name("Test Author")
          .type(AuthorType.AUTHOR)
          .build();

      assertThrows(NullPointerException.class, () -> scraperBase.syncAuthors(null, List.of(author)));
    }

    @Test
    void shouldThrowExceptionWhenAuthorsIsNull() {
      assertThrows(NullPointerException.class, () -> scraperBase.syncAuthors(work, null));
    }

    @Test
    void shouldInitializeAuthorsListWhenNull() {
      work.setAuthors(null);
      var author = Author.builder()
          .name("Test Author")
          .type(AuthorType.AUTHOR)
          .build();

      when(authorService.findOrCreate(author))
          .thenReturn(author);

      scraperBase.syncAuthors(work, List.of(author));

      assertNotNull(work.getAuthors());
      assertEquals(1, work.getAuthors().size());
    }
  }

  @Nested
  @DisplayName("syncCover")
  class SyncCoverTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
      work.setCovers(new ArrayList<>());
      work.setSlug("test-slug");
      work.setPublicationDemographic(WorkPublicationDemographic.SHOUNEN);
    }

    @Test
    void shouldSyncNewCover() throws IOException, InterruptedException {
      var scanlator = Scanlator.builder().code("mediocre").build();

      when(scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(Optional.of(scanlator));
      doNothing().when(externalFileService).downloadExternalPublicObjectAndUploadToStorage(
          anyString(), anyString(), anyString());

      scraperBase.syncCover(work, "https://example.com/cover.jpg", "cover.jpg",
          SynchronizationOriginType.MEDIOCRESCAN, CoverType.HIGH);

      assertEquals(1, work.getCovers().size());
      verify(externalFileService).downloadExternalPublicObjectAndUploadToStorage(
          eq("https://example.com/cover.jpg"), anyString(), anyString());
    }

    @Test
    void shouldSkipDuplicateCover() throws IOException, InterruptedException {
      var cover = WorkCover.builder()
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .size(CoverType.HIGH)
          .work(work)
          .build();
      work.getCovers().add(cover);

      scraperBase.syncCover(work, "https://example.com/cover.jpg", "cover.jpg",
          SynchronizationOriginType.MEDIOCRESCAN, CoverType.HIGH);

      assertEquals(1, work.getCovers().size());
      verify(externalFileService, times(0)).downloadExternalPublicObjectAndUploadToStorage(
          anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncCover(null, "url", "file.jpg", SynchronizationOriginType.MEDIOCRESCAN, CoverType.HIGH));
    }

    @Test
    void shouldInitializeCoversListWhenNull() throws IOException, InterruptedException {
      work.setCovers(null);
      var scanlator = Scanlator.builder().code("mediocre").build();

      when(scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(Optional.of(scanlator));
      doNothing().when(externalFileService).downloadExternalPublicObjectAndUploadToStorage(
          anyString(), anyString(), anyString());

      scraperBase.syncCover(work, "https://example.com/cover.jpg", "cover.jpg",
          SynchronizationOriginType.MEDIOCRESCAN, CoverType.HIGH);

      assertNotNull(work.getCovers());
      assertEquals(1, work.getCovers().size());
    }
  }

  @Nested
  @DisplayName("syncRelationship")
  class SyncRelationshipTests {

    private Work work;

    @BeforeEach
    void setUp() {
      work = Work.builder().build();
    }

    @Test
    void shouldSyncRelationship() {
      var relatedWork = Work.builder().id(java.util.UUID.randomUUID()).build();

      when(workService.findBySynchronizationExternalID("ext456", SynchronizationOriginType.MEDIOCRESCAN))
          .thenReturn(Optional.of(relatedWork));

      scraperBase.syncRelationship(work, SynchronizationOriginType.MEDIOCRESCAN, "ext456");

      assertEquals(relatedWork, work.getRelationship());
      assertEquals(work, relatedWork.getRelationship());
      verify(workService, times(2)).save(any(Work.class));
    }

    @Test
    void shouldNotOverrideExistingRelationship() {
      var existingRelated = Work.builder().id(java.util.UUID.randomUUID()).build();
      work.setRelationship(existingRelated);

      scraperBase.syncRelationship(work, SynchronizationOriginType.MEDIOCRESCAN, "ext456");

      assertEquals(existingRelated, work.getRelationship());
      verify(workService, times(0)).save(any(Work.class));
    }

    @Test
    void shouldThrowExceptionWhenExternalIdIsBlank() {
      assertThrows(IllegalArgumentException.class,
          () -> scraperBase.syncRelationship(work, SynchronizationOriginType.MEDIOCRESCAN, "   "));
    }

    @Test
    void shouldThrowExceptionWhenWorkIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncRelationship(null, SynchronizationOriginType.MEDIOCRESCAN, "ext456"));
    }

    @Test
    void shouldThrowExceptionWhenOriginIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncRelationship(work, null, "ext456"));
    }

    @Test
    void shouldThrowExceptionWhenExternalIdIsNull() {
      assertThrows(NullPointerException.class,
          () -> scraperBase.syncRelationship(work, SynchronizationOriginType.MEDIOCRESCAN, null));
    }
  }

  @Nested
  @DisplayName("jwtExtractExpiration")
  class JwtExtractExpirationTests {

    @Test
    void shouldExtractExpirationFromValidJwt() {
      var jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzgyNjQ3MDF9.test";

      long exp = scraperBase.jwtExtractExpiration(jwt);

      assertEquals(1638264701L, exp);
    }

    @Test
    void shouldThrowBusinessExceptionOnInvalidJwt() {
      var invalidJwt = "invalid.jwt.token";

      assertThrows(BusinessException.class, () -> scraperBase.jwtExtractExpiration(invalidJwt));
    }

    @Test
    void shouldThrowBusinessExceptionOnMissingExpClaim() {
      var jwtWithoutExp = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.test";

      assertThrows(BusinessException.class, () -> scraperBase.jwtExtractExpiration(jwtWithoutExp));
    }
  }

  @Nested
  @DisplayName("isJwtTokenExpiring")
  class IsJwtTokenExpiringTests {

    @Test
    void shouldReturnTrueWhenTokenIsExpiring() {
      var now = java.time.Instant.now().getEpochSecond();
      var expiringIn30Seconds = now + 30;

      boolean result = scraperBase.isJwtTokenExpiring("token", expiringIn30Seconds);

      assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenTokenIsExpired() {
      var now = java.time.Instant.now().getEpochSecond();
      var expiredTime = now - 1000;

      boolean result = scraperBase.isJwtTokenExpiring("token", expiredTime);

      assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsValid() {
      var now = java.time.Instant.now().getEpochSecond();
      var validUntil = now + 200;

      boolean result = scraperBase.isJwtTokenExpiring("token", validUntil);

      assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenAccessTokenIsNull() {
      boolean result = scraperBase.isJwtTokenExpiring(null, 1000);

      assertTrue(result);
    }
  }
}
