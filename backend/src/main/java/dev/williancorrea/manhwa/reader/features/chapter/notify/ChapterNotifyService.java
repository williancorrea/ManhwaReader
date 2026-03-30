package dev.williancorrea.manhwa.reader.features.chapter.notify;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import dev.williancorrea.manhwa.reader.config.email.EmailConfigKey;
import dev.williancorrea.manhwa.reader.email.EmailService;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.system.SystemConfigurationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ChapterNotifyService {

  private final ChapterNotifyRepository repository;
  private final SystemConfigurationService systemConfigurationService;

  @Lazy
  private final EmailService emailService;

  @Value("${minio.url.public}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  @Transactional
  public void processAndSendNotifications() {
    if (systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()).equals("false")) {
      log.warn("[ChapterNotifyService][processAndSendNotifications] Email sending is disabled.");
      return;
    }


    log.debug("[ChapterNotifyService][processAndSendNotifications] Starting notification processing");

    List<ChapterNotify> allNotifications = repository.findAllWithWorkAndChapter();

    if (allNotifications.isEmpty()) {
      log.debug("[ChapterNotifyService][processAndSendNotifications] No notifications to process");
      return;
    }

    // Group notifications by project.
    Map<Work, List<ChapterNotify>> notificationsByWork = allNotifications.stream()
        .collect(Collectors.groupingBy(ChapterNotify::getWork));

    log.debug("[ChapterNotifyService][processAndSendNotifications] Found {} works with notifications",
        notificationsByWork.size());

    List<ChapterNotify> processedNotifications = new ArrayList<>();

    // Process each work
    notificationsByWork.forEach((work, notifications) -> {
      try {
        sendWorkNotifications(work, notifications);
        processedNotifications.addAll(notifications);
        log.info("[ChapterNotifyService][processAndSendNotifications] Processed {} notifications for work: {}",
            notifications.size(), work.getId());
      } catch (Exception e) {
        log.error("[ChapterNotifyService][processAndSendNotifications] Error processing notifications for work: {}",
            work.getId(), e);
      }
    });

    // Remove processed notifications
    if (!processedNotifications.isEmpty()) {
      List<UUID> processedIds = processedNotifications.stream()
          .map(ChapterNotify::getId)
          .toList();
      repository.deleteByIdIn(processedIds);
      log.debug("[ChapterNotifyService][processAndSendNotifications] Deleted {} processed notifications",
          processedIds.size());
    }

    log.debug("[ChapterNotifyService][processAndSendNotifications] Notification processing completed");
  }

  private void sendWorkNotifications(Work work, List<ChapterNotify> notifications) {
    if (systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()).equals("false")) {
      log.warn("[ChapterNotifyService][sendWorkNotifications] Email sending is disabled.");
      return;
    }

    String workTitle = "(" + work.getPublicationDemographic() + ") " + getWorkTitle(work);
    int chapterCount = notifications.size();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    List<Map<String, Object>> chapters = notifications.stream()
        .map(notification -> {
          Map<String, Object> chapterData = new HashMap<>();
          chapterData.put("title", "Capítulo " + notification.getChapter().getNumberWithVersionInteger());
          chapterData.put("publishedAt", notification.getChapter().getPublishedAt() != null
              ? notification.getChapter().getPublishedAt().format(formatter)
              : "");
          chapterData.put("notificationType", notification.getStatus().name());
          return chapterData;
        })
        .toList();

    Map<String, Object> additionalData = new HashMap<>();
    additionalData.put("coverUrl", work.getCoverUrl() != null
        ? minioUrl + "/" + bucketName + work.getCoverUrl()
        : ""
    );

    String scanlatorName = notifications.stream()
        .findFirst()
        .map(n -> n.getChapter().getScanlator())
        .map(Scanlator::getName)
        .orElse("");
    additionalData.put("scanlator", scanlatorName);

    emailService.sendNewChaptersEmail(workTitle, chapterCount, chapters, additionalData);

    log.info("[ChapterNotifyService][sendWorkNotifications] Email sent for work: {} with {} chapters",
        workTitle, chapterCount);
  }

  private String getWorkTitle(Work work) {
    if (work.getTitles() != null && !work.getTitles().isEmpty()) {
      return work.getTitles().getFirst().getTitle();
    }
    return "Título Desconhecido";
  }

  @Transactional
  public ChapterNotify save(ChapterNotify entity) {
    return repository.save(entity);
  }
}


