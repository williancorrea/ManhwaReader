package dev.williancorrea.manhwa.reader.scraper.base;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.williancorrea.manhwa.reader.email.EmailService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScraperHelper")
class ScraperHelperTest {

  @Mock
  private EmailService emailService;

  private ScraperHelper scraperHelper;

  @BeforeEach
  void setUp() {
    scraperHelper = new ScraperHelper(emailService);
    ReflectionTestUtils.setField(scraperHelper, "minioUrl", "http://minio.example.com");
    ReflectionTestUtils.setField(scraperHelper, "bucketName", "manhwa-covers");
  }

  @Nested
  @DisplayName("notifyWorkAdded")
  class NotifyWorkAddedTests {

    @Test
    void shouldSendNotificationWhenWorkIsValid() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .type(null)
          .status(WorkStatus.ONGOING)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .createdAt(OffsetDateTime.now())
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldSendNotificationWithAllData() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .type(null)
          .status(WorkStatus.ONGOING)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .createdAt(OffsetDateTime.now())
          .build();

      var title = WorkTitle.builder()
          .title("Complete Manhwa")
          .work(work)
          .build();

      var synopsis = mock(dev.williancorrea.manhwa.reader.features.work.WorkSynopsis.class);

      work.setTitles(List.of(title));
      work.setSynopses(List.of(synopsis));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MANGADEX));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldNotSendNotificationWhenWorkIsNull() {
      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(null, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService, never()).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldNotSendNotificationWhenTitlesAreNull() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();
      work.setTitles(null);

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService, never()).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldNotSendNotificationWhenTitlesAreEmpty() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();
      work.setTitles(new ArrayList<>());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService, never()).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldHandleEmailServiceException() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .type(null)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doThrow(new RuntimeException("Email service error"))
          .when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldIncludeWorkTypeWhenPresent() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .type(dev.williancorrea.manhwa.reader.features.work.WorkType.MANHWA)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldIncludeStatusWhenPresent() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .status(WorkStatus.COMPLETED)
          .publicationDemographic(WorkPublicationDemographic.SHOUJO)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MANGADEX));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldIncludeCreatedAtWhenPresent() {
      var now = OffsetDateTime.now();
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .createdAt(now)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldIncludeDescriptionWhenSynopsesPresent() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();

      var synopsis = mock(dev.williancorrea.manhwa.reader.features.work.WorkSynopsis.class);

      work.setTitles(List.of(title));
      work.setSynopses(List.of(synopsis));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MANGOTOONS));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldHandleWorkWithCoversCollection() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();

      var cover = mock(dev.williancorrea.manhwa.reader.features.work.cover.WorkCover.class);
      when(cover.getIsOfficial()).thenReturn(Boolean.TRUE);

      work.setTitles(List.of(title));
      work.setCovers(List.of(cover));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.LYCANTOONS));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldNotIncludeSynopsesWhenEmpty() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));
      work.setSynopses(new ArrayList<>());

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MEDIOCRESCAN));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }

    @Test
    void shouldUseOriginNameWhenScanlatorNotFound() {
      var work = Work.builder()
          .id(java.util.UUID.randomUUID())
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      var title = WorkTitle.builder()
          .title("Test Manhwa")
          .work(work)
          .build();
      work.setTitles(List.of(title));

      doNothing().when(emailService).sendWorkAddedEmail(anyString(), any());

      assertDoesNotThrow(() ->
          scraperHelper.notifyWorkAdded(work, SynchronizationOriginType.MANGOTOONS));

      verify(emailService).sendWorkAddedEmail(anyString(), any());
    }
  }
}
