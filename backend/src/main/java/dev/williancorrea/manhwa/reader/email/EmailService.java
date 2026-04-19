package dev.williancorrea.manhwa.reader.email;

import java.util.Map;
import dev.williancorrea.manhwa.reader.config.email.EmailConfigKey;
import dev.williancorrea.manhwa.reader.system.SystemConfigurationService;
import dev.williancorrea.manhwa.reader.utils.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final @Lazy SystemConfigurationService systemConfigurationService;

  @Async("emailTaskExecutor")
  public void sendEmail(EmailData emailData) {
    if (systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()).equals("false")) {
      log.info("[EmailService][sendEmail] Email sending is disabled. Email not sent to: {}", emailData.getTo());
      return;
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(
          systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()),
          systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()));
      helper.setTo(emailData.getTo());

      if (emailData.getEmailType() == EmailType.NEW_CHAPTERS) {
        helper.setSubject(emailData.getEmailType().getSubject()
            + " - " + emailData.getVariables().get("chaptersSize")
            + " - " + emailData.getVariables().get("workTitle"));
      } else {
        helper.setSubject(emailData.getEmailType().getSubject()
            + " - " + emailData.getVariables().get("workTitle"));
      }


      String htmlContent = processTemplate(emailData.getEmailType(), emailData.getVariables());
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("[EmailService][sendEmail] Email sent successfully to: {} with type: {}", emailData.getTo(),
          emailData.getEmailType());

    } catch (MessagingException e) {
      log.error("[EmailService][sendEmail] Error sending email to: {} with type: {}", emailData.getTo(),
          emailData.getEmailType(), e);
    } catch (Exception e) {
      log.error("[EmailService][sendEmail] Unexpected error sending email to: {}", emailData.getTo(), e);
    }
  }

  public void sendWorkAddedEmail(String workTitle, Map<String, Object> additionalData) {
    EmailData emailData = EmailData.builder()
        .to(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of(
            "workTitle", workTitle,
            "coverUrl", additionalData.getOrDefault("coverUrl", ""),
            "workType", additionalData.getOrDefault("workType", ""),
            "status", additionalData.getOrDefault("status", ""),
            "scanlator", additionalData.getOrDefault("scanlator", ""),
            "addedAt", additionalData.getOrDefault("addedAt", ""),
            "description", additionalData.getOrDefault("description", "")
        ))
        .build();

    sendEmail(emailData);
  }

  public void sendNewChaptersEmail(String workTitle, int chapterCount,
                                   java.util.List<Map<String, Object>> chapters,
                                   Map<String, Object> additionalData) {
    EmailData emailData = EmailData.builder()
        .to(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .emailType(EmailType.NEW_CHAPTERS)
        .variables(Map.of(
            "workTitle", workTitle,
            "coverUrl", additionalData.getOrDefault("coverUrl", ""),
            "chapterCount", chapterCount,
            "chapters", chapters,
            "chaptersSize", StringUtils.completeWithZeroZeroToLeft(String.valueOf(chapters.size()),3),
            "scanlator", additionalData.getOrDefault("scanlator", "")
        ))
        .build();

    sendEmail(emailData);
  }

  public void sendScraperErrorEmail(String scraperName, String errorMessage,
                                    Map<String, Object> errorDetails) {
    EmailData emailData = EmailData.builder()
        .to(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .emailType(EmailType.SCRAPER_ERROR)
        .variables(Map.of(
            "scraperName", scraperName,
            "errorMessage", errorMessage,
            "workTitle", errorDetails.getOrDefault("workTitle", "N/A"),
            "coverUrl", errorDetails.getOrDefault("coverUrl", ""),
            "errorTime", errorDetails.getOrDefault("errorTime", java.time.LocalDateTime.now().toString()),
            "errorType", errorDetails.getOrDefault("errorType", "Unknown"),
            "stackTrace", errorDetails.getOrDefault("stackTrace", ""),
            "url", errorDetails.getOrDefault("url", "")
        ))
        .build();

    sendEmail(emailData);
  }

  private String processTemplate(EmailType emailType, Map<String, Object> variables) {
    Context context = new Context();
    if (variables != null) {
      variables.forEach(context::setVariable);
    }

    String templatePath = "email/" + emailType.getTemplateName();
    return templateEngine.process(templatePath, context);
  }
}
