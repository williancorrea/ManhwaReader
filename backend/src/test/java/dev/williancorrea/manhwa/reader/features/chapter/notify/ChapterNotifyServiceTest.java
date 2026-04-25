package dev.williancorrea.manhwa.reader.features.chapter.notify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.williancorrea.manhwa.reader.config.email.EmailConfigKey;
import dev.williancorrea.manhwa.reader.email.EmailService;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.system.SystemConfigurationService;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChapterNotifyService")
class ChapterNotifyServiceTest {

  @Mock
  private ChapterNotifyRepository repository;

  @Mock
  private SystemConfigurationService systemConfigurationService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private ChapterNotifyService chapterNotifyService;

  @Captor
  private ArgumentCaptor<List<UUID>> deleteIdsCaptor;

  private UUID workId;
  private UUID chapterId;
  private Work testWork;
  private Chapter testChapter;
  private ChapterNotify testNotification;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    chapterId = UUID.randomUUID();

    testWork = Work.builder()
        .id(workId)
        .slug("test-work")
        .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
        .titles(List.of(
            WorkTitle.builder().title("Test Work").isOfficial(true).build()
        ))
        .build();

    Language language = Language.builder()
        .id(UUID.randomUUID())
        .code("EN")
        .build();
    Scanlator scanlator = Scanlator.builder()
        .id(UUID.randomUUID())
        .name("TestScanlator")
        .code("TS")
        .build();

    testChapter = Chapter.builder()
        .id(chapterId)
        .work(testWork)
        .number("1")
        .numberFormatted("0001")
        .numberVersion("0000")
        .language(language)
        .scanlator(scanlator)
        .publishedAt(OffsetDateTime.now())
        .synced(true)
        .build();

    testNotification = ChapterNotify.builder()
        .id(UUID.randomUUID())
        .chapter(testChapter)
        .work(testWork)
        .status(ChapterNotifyType.NEW)
        .createdAt(OffsetDateTime.now())
        .build();

    ReflectionTestUtils.setField(chapterNotifyService, "minioUrl", "https://storage.example.com");
    ReflectionTestUtils.setField(chapterNotifyService, "bucketName", "manhwa");
  }

  @Nested
  @DisplayName("processAndSendNotifications()")
  class ProcessAndSendNotificationsTests {

    @Test
    @DisplayName("should not process when email is disabled")
    void shouldNotProcessWhenEmailIsDisabled() {
      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("false");

      chapterNotifyService.processAndSendNotifications();

      verify(repository, never()).findAllWithWorkAndChapter();
      verify(emailService, never()).sendNewChaptersEmail(anyString(), anyInt(), anyList(), any());
    }

    @Test
    @DisplayName("should process notifications when email is enabled")
    void shouldProcessNotificationsWhenEmailIsEnabled() {
      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(List.of(testNotification));

      chapterNotifyService.processAndSendNotifications();

      verify(repository, times(1)).findAllWithWorkAndChapter();
      verify(emailService, times(1)).sendNewChaptersEmail(anyString(), anyInt(), anyList(), any());
    }

    @Test
    @DisplayName("should skip processing when no notifications exist")
    void shouldSkipProcessingWhenNoNotificationsExist() {
      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(Collections.emptyList());

      chapterNotifyService.processAndSendNotifications();

      verify(repository, times(1)).findAllWithWorkAndChapter();
    }

    @Test
    @DisplayName("should group notifications by work")
    void shouldGroupNotificationsByWork() {
      Work work2 = Work.builder()
          .id(UUID.randomUUID())
          .slug("work-2")
          .publicationDemographic(WorkPublicationDemographic.SHOUJO)
          .titles(List.of(WorkTitle.builder().title("Work 2").isOfficial(true).build()))
          .build();
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(work2)
          .number("1")
          .numberFormatted("0001")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).code("EN").build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).name("TS").code("TS").build())
          .build();
      ChapterNotify notification2 = ChapterNotify.builder()
          .id(UUID.randomUUID())
          .chapter(chapter2)
          .work(work2)
          .status(ChapterNotifyType.NEW)
          .build();

      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(List.of(testNotification, notification2));

      chapterNotifyService.processAndSendNotifications();

      verify(emailService, times(2)).sendNewChaptersEmail(anyString(), anyInt(), anyList(), any());
    }

    @Test
    @DisplayName("should delete processed notifications")
    void shouldDeleteProcessedNotifications() {
      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(List.of(testNotification));

      chapterNotifyService.processAndSendNotifications();

      verify(repository, times(1)).deleteByIdIn(deleteIdsCaptor.capture());
      List<UUID> deletedIds = deleteIdsCaptor.getValue();
      assert deletedIds.contains(testNotification.getId());
    }

    @Test
    @DisplayName("should process and delete successful notifications")
    void shouldProcessAndDeleteSuccessfulNotifications() {
      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(List.of(testNotification));

      chapterNotifyService.processAndSendNotifications();

      verify(repository, times(1)).deleteByIdIn(deleteIdsCaptor.capture());
      List<UUID> deletedIds = deleteIdsCaptor.getValue();
      assertEquals(1, deletedIds.size());
      assertEquals(testNotification.getId(), deletedIds.get(0));
    }

    @Test
    @DisplayName("should process multiple notifications for same work")
    void shouldProcessMultipleNotificationsForSameWork() {
      Chapter chapter2 = Chapter.builder()
          .id(UUID.randomUUID())
          .work(testWork)
          .number("2")
          .numberFormatted("0002")
          .numberVersion("0000")
          .language(Language.builder().id(UUID.randomUUID()).code("EN").build())
          .scanlator(Scanlator.builder().id(UUID.randomUUID()).name("TS").code("TS").build())
          .build();
      ChapterNotify notification2 = ChapterNotify.builder()
          .id(UUID.randomUUID())
          .chapter(chapter2)
          .work(testWork)
          .status(ChapterNotifyType.NEW)
          .build();

      when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
          .thenReturn("true");
      when(repository.findAllWithWorkAndChapter()).thenReturn(List.of(testNotification, notification2));

      chapterNotifyService.processAndSendNotifications();

      verify(emailService, times(1)).sendNewChaptersEmail(anyString(), eq(2), anyList(), any());
    }
  }


  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save notification")
    void shouldSaveNotification() {
      when(repository.save(testNotification)).thenReturn(testNotification);

      ChapterNotify result = chapterNotifyService.save(testNotification);

      assert result.equals(testNotification);
      verify(repository, times(1)).save(testNotification);
    }

    @Test
    @DisplayName("should save notification with all fields")
    void shouldSaveNotificationWithAllFields() {
      when(repository.save(testNotification)).thenReturn(testNotification);

      chapterNotifyService.save(testNotification);

      verify(repository, times(1)).save(testNotification);
    }
  }

}
