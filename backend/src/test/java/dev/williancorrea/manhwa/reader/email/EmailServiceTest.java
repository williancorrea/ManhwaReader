package dev.williancorrea.manhwa.reader.email;

import dev.williancorrea.manhwa.reader.config.email.EmailConfigKey;
import dev.williancorrea.manhwa.reader.system.SystemConfigurationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private TemplateEngine templateEngine;

  @Mock
  private SystemConfigurationService systemConfigurationService;

  @Mock
  private MimeMessage mimeMessage;

  @InjectMocks
  private EmailService emailService;

  @Test
  void shouldNotSendEmailWhenEmailIsDisabled() {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("false");

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Manga Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender, never()).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendEmailWhenEmailIsEnabled() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Email content</html>");

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Manga Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSetSubjectForNewChaptersEmailType() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Chapters</html>");

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.NEW_CHAPTERS)
        .variables(Map.of(
            "workTitle", "Manga Title",
            "chaptersSize", "003"
        ))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSetSubjectForOtherEmailTypes() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Error</html>");

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.SCRAPER_ERROR)
        .variables(Map.of("workTitle", "Manga Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldHandleMessagingExceptionGracefully() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Content</html>");
    doNothing().when(mailSender).send(ArgumentMatchers.<MimeMessage>any());

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendWorkAddedEmail() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/work-added"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Work added</html>");

    Map<String, Object> additionalData = Map.of(
        "coverUrl", "https://example.com/cover.jpg",
        "workType", "Manhwa",
        "status", "ONGOING",
        "scanlator", "MyTeam",
        "addedAt", "2026-04-21",
        "description", "A great work"
    );

    emailService.sendWorkAddedEmail("My Manhwa", additionalData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendWorkAddedEmailWithMissingAdditionalData() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/work-added"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Work added</html>");

    emailService.sendWorkAddedEmail("My Manhwa", Map.of());

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendNewChaptersEmail() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/new-chapters"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>New chapters</html>");

    List<Map<String, Object>> chapters = List.of(
        Map.of("number", "1", "title", "Chapter 1"),
        Map.of("number", "2", "title", "Chapter 2"),
        Map.of("number", "3", "title", "Chapter 3")
    );

    Map<String, Object> additionalData = Map.of(
        "coverUrl", "https://example.com/cover.jpg",
        "scanlator", "MyTeam"
    );

    emailService.sendNewChaptersEmail("My Manhwa", 3, chapters, additionalData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendNewChaptersEmailWithFormattedChaptersSize() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/new-chapters"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>New chapters</html>");

    List<Map<String, Object>> chapters = List.of(
        Map.of("number", "1", "title", "Chapter 1")
    );

    emailService.sendNewChaptersEmail("My Manhwa", 1, chapters, Map.of());

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendScraperErrorEmail() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/scraper-error"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Error report</html>");

    Map<String, Object> errorDetails = Map.of(
        "workTitle", "My Manhwa",
        "coverUrl", "https://example.com/cover.jpg",
        "errorTime", "2026-04-21T10:30:00",
        "errorType", "NETWORK_ERROR",
        "stackTrace", "Exception at line 42...",
        "url", "https://source.com/work/123"
    );

    emailService.sendScraperErrorEmail("MediacreScanner", "Connection timeout", errorDetails);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendScraperErrorEmailWithDefaultValues() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/scraper-error"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Error report</html>");

    emailService.sendScraperErrorEmail("MyScraperName", "Error occurred", Map.of());

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldHandleExceptionWhenCreatingMimeMessage() {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender, never()).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldHandleMailSendException() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Content</html>");
    doThrow(new MailSendException("Failed to send")).when(mailSender).send(ArgumentMatchers.<MimeMessage>any());

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldHandleRuntimeExceptionDuringSend() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(ArgumentMatchers.<String>any(), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>Content</html>");
    doThrow(new RuntimeException("Unexpected error during send")).when(mailSender).send(ArgumentMatchers.<MimeMessage>any());

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(Map.of("workTitle", "Title"))
        .build();

    emailService.sendEmail(emailData);

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

  @Test
  void shouldSendNewChaptersEmailWithEmptyChaptersList() throws MessagingException {
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ENABLED.name()))
        .thenReturn("true");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM.name()))
        .thenReturn("noreply@example.com");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_FROM_NAME.name()))
        .thenReturn("Manhwa Reader");
    when(systemConfigurationService.getValueByReference(EmailConfigKey.EMAIL_ADMIN.name()))
        .thenReturn("admin@example.com");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email/new-chapters"), ArgumentMatchers.<Context>any()))
        .thenReturn("<html>New chapters</html>");

    emailService.sendNewChaptersEmail("My Manhwa", 0, List.of(), Map.of());

    verify(mailSender).send(ArgumentMatchers.<MimeMessage>any());
  }

}
