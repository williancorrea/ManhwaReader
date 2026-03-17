package dev.williancorrea.manhwa.reader.config.email;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import dev.williancorrea.manhwa.reader.system.SystemConfiguration;
import dev.williancorrea.manhwa.reader.system.SystemConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

  @Lazy
  private final SystemConfigurationService systemConfigService;

  @Bean
  public JavaMailSender javaMailSender() {
    var mailSender = new JavaMailSenderImpl();
    var config = systemConfigService.findByGroupName("EMAIL")
        .stream()
        .collect(Collectors.toMap(SystemConfiguration::getReference, SystemConfiguration::getValue));

    // Basic configuration - usando getConfigValue para buscar por reference
    mailSender.setHost(getConfigValue(config, EmailConfigKey.EMAIL_SMTP_HOST));
    mailSender.setPort(Integer.parseInt(getConfigValue(config, EmailConfigKey.EMAIL_SMTP_PORT)));
    mailSender.setUsername(getConfigValue(config, EmailConfigKey.EMAIL_SMTP_USERNAME));
    mailSender.setPassword(getConfigValue(config, EmailConfigKey.EMAIL_SMTP_PASSWORD));
    mailSender.setDefaultEncoding("UTF-8");

    // Extra properties
    Properties props = mailSender.getJavaMailProperties();
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_PROTOCOL);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_AUTH);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_STARTTLS_ENABLE);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_STARTTLS_REQUIRED);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_CONNECTION_TIMEOUT);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_TIMEOUT);
    addProperty(props, config, EmailConfigKey.EMAIL_SMTP_WRITE_TIMEOUT);
    addProperty(props, config, EmailConfigKey.EMAIL_DEBUG);

    return mailSender;
  }

  private String getConfigValue(Map<String, String> config, EmailConfigKey key) {
    return config.getOrDefault(key.name(), key.getDefaultValue());
  }

  private void addProperty(Properties props, Map<String, String> config, EmailConfigKey key) {
    String value = getConfigValue(config, key);
    props.put(key.getReference(), value);
  }
}
