package dev.williancorrea.manhwa.reader.scraper.base;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import dev.williancorrea.manhwa.reader.email.EmailService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperHelper {

  private final EmailService emailService;

  @Value("${minio.url}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  /**
   * Send notification email when a new work is added
   */
  public void notifyWorkAdded(Work work) {
    try {
      if (work == null || work.getTitles() == null || work.getTitles().isEmpty()) {
        log.warn("Cannot send work added notification: work or title is null");
        return;
      }

      var workTitle = work.getTitles().get(0).getTitle();
      var additionalData = new HashMap<String, Object>();

      if (work.getType() != null) {
        additionalData.put("workType", work.getType().name());
      }

      if (work.getOriginalLanguage() != null) {
        additionalData.put("language", work.getOriginalLanguage().getName());
      }

      if (work.getSynchronizations() != null && !work.getSynchronizations().isEmpty()) {
        var scanlator = work.getSynchronizations().get(0).getOrigin().name();
        additionalData.put("scanlator", scanlator);
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

  /**
   * Send notification email when new chapters are added to a work
   */
  public void notifyNewChapters(Work work, int chapterCount) {
    try {
      if (work == null || work.getTitles() == null || work.getTitles().isEmpty()) {
        log.warn("Cannot send new chapters notification: work or title is null");
        return;
      }

      var workTitle = work.getTitles().get(0).getTitle();
      var chapters = new java.util.ArrayList<Map<String, Object>>();

      // Get recent chapters (limit to last 5)
      if (work.getChapters() != null) {
        work.getChapters().stream()
            .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .limit(5)
            .forEach(chapter -> {
              var chapterData = new HashMap<String, Object>();
              chapterData.put("title",
                  chapter.getTitle() != null ? chapter.getTitle() : "Capítulo " + chapter.getNumberFormatted());
              if (chapter.getPublishedAt() != null) {
                chapterData.put("publishedAt",
                    chapter.getPublishedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
              }
              chapters.add(chapterData);
            });
      }

      var additionalData = new HashMap<String, Object>();
      if (work.getSynchronizations() != null && !work.getSynchronizations().isEmpty()) {
        var scanlator = work.getSynchronizations().get(0).getOrigin().name();
        additionalData.put("scanlator", scanlator);
      }

      // Adiciona URL da capa
      String coverUrl = work.getCoverUrl();
      if (coverUrl != null) {
        additionalData.put("coverUrl",  minioUrl + "/" + bucketName + coverUrl);
      }

      emailService.sendNewChaptersEmail(workTitle, chapterCount, chapters, additionalData);
      log.info("New chapters notification sent for: {} ({} chapters)", workTitle, chapterCount);
    } catch (Exception e) {
      log.error("Failed to send new chapters notification", e);
    }
  }
}
