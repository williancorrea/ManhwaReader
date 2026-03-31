package dev.williancorrea.manhwa.reader.scraper.base;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import dev.williancorrea.manhwa.reader.email.EmailService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperHelper {

  private final EmailService emailService;

  @Value("${minio.url.public}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  /**
   * Send notification email when a new work is added
   */
  public void notifyWorkAdded(Work work, SynchronizationOriginType origin) {
    try {
      if (work == null || work.getTitles() == null || work.getTitles().isEmpty()) {
        log.warn("Cannot send work added notification: work or title is null");
        return;
      }

      var workTitle = "(" + work.getPublicationDemographic() + ") " + work.getTitles().get(0).getTitle();
      var additionalData = new HashMap<String, Object>();
      additionalData.put("scanlator", origin.name());

      if (work.getType() != null) {
        additionalData.put("workType", work.getType().name());
      }

      if (work.getStatus() != null) {
        additionalData.put("status", work.getStatus().name());
      }

      if (work.getCreatedAt() != null) {
        additionalData.put("addedAt", work.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
      }

      if (work.getSynopses() != null && !work.getSynopses().isEmpty()) {
        additionalData.put("description", work.getSynopses().get(0).getDescription());
      }

      // Adiciona URL da capa
      String coverUrl = work.getCoverUrl();
      if (coverUrl != null) {
        additionalData.put("coverUrl", minioUrl + "/" + bucketName + coverUrl);
      }

      emailService.sendWorkAddedEmail(workTitle, additionalData);
      log.info("Work added notification sent for: {}", workTitle);
    } catch (Exception e) {
      log.error("Failed to send work added notification", e);
    }
  }
}
