package dev.williancorrea.manhwa.reader.email;

import dev.williancorrea.manhwa.reader.config.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final EmailProperties emailProperties;

  @Async
  public void sendEmail(EmailData emailData) {
    if (!emailProperties.isEnabled()) {
      log.info("Email sending is disabled. Email not sent to: {}", emailData.getTo());
      return;
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
      helper.setTo(emailData.getTo());
      helper.setSubject(emailData.getEmailType().getSubject());

      String htmlContent = processTemplate(emailData.getEmailType(), emailData.getVariables());
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Email sent successfully to: {} with type: {}", emailData.getTo(), emailData.getEmailType());

    } catch (MessagingException e) {
      log.error("Error sending email to: {} with type: {}", emailData.getTo(), emailData.getEmailType(), e);
    } catch (Exception e) {
      log.error("Unexpected error sending email to: {}", emailData.getTo(), e);
    }
  }

  public void sendWorkAddedEmail(String workTitle, Map<String, Object> additionalData) {
    EmailData emailData = EmailData.builder()
        .to(emailProperties.getAdminEmail())
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of(
            "workTitle", workTitle,
            "workType", additionalData.getOrDefault("workType", ""),
            "language", additionalData.getOrDefault("language", ""),
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
        .to(emailProperties.getAdminEmail())
        .emailType(EmailType.NEW_CHAPTERS)
        .variables(Map.of(
            "workTitle", workTitle,
            "chapterCount", chapterCount,
            "chapters", chapters,
            "scanlator", additionalData.getOrDefault("scanlator", "")
        ))
        .build();

    sendEmail(emailData);
  }

  public void sendScraperErrorEmail(String scraperName, String errorMessage,
                                    Map<String, Object> errorDetails) {
    EmailData emailData = EmailData.builder()
        .to(emailProperties.getAdminEmail())
        .emailType(EmailType.SCRAPER_ERROR)
        .variables(Map.of(
            "scraperName", scraperName,
            "errorMessage", errorMessage,
            "workTitle", errorDetails.getOrDefault("workTitle", "N/A"),
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
