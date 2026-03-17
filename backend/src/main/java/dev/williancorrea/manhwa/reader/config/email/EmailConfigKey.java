package dev.williancorrea.manhwa.reader.config.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailConfigKey {

  EMAIL_ENABLED("email.enabled", "true"),

  // SMTP Configuration
  EMAIL_SMTP_PROTOCOL("mail.transport.protocol", "smtp"),
  EMAIL_SMTP_HOST("mail.smtp.host", "smtp.gmail.com"),
  EMAIL_SMTP_PORT("mail.smtp.port", "587"),
  EMAIL_SMTP_USERNAME("mail.smtp.username", ""),
  EMAIL_SMTP_PASSWORD("mail.smtp.password", ""),
  EMAIL_SMTP_AUTH("mail.smtp.auth", "true"),
  EMAIL_SMTP_STARTTLS_ENABLE("mail.smtp.starttls.enable", "true"),
  EMAIL_SMTP_STARTTLS_REQUIRED("mail.smtp.starttls.required", "true"),

  EMAIL_SMTP_CONNECTION_TIMEOUT("mail.smtp.connectiontimeout", "5000"),
  EMAIL_SMTP_WRITE_TIMEOUT("mail.smtp.writetimeout", "5000"),
  EMAIL_SMTP_TIMEOUT("mail.smtp.timeout", "5000"),
  EMAIL_DEBUG("mail.debug", "false"),

  // Email Configuration
  EMAIL_FROM("email.from", "noreply@manhwareader.com"),
  EMAIL_FROM_NAME("email.from.name", "Manhwa Reader"),
  EMAIL_ADMIN("email.admin", "admin@manhwareader.com");

  private final String reference;
  private final String defaultValue;
}
