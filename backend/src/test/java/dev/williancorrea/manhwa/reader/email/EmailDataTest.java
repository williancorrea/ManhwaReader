package dev.williancorrea.manhwa.reader.email;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmailDataTest {

  @Test
  void shouldBuildEmailDataWithAllFields() {
    Map<String, Object> variables = new HashMap<>();
    variables.put("name", "John");
    variables.put("link", "https://example.com");

    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(variables)
        .build();

    assertThat(emailData)
        .isNotNull()
        .hasFieldOrPropertyWithValue("to", "user@example.com")
        .hasFieldOrPropertyWithValue("emailType", EmailType.WORK_ADDED)
        .hasFieldOrPropertyWithValue("variables", variables);
  }

  @Test
  void shouldBuildEmailDataWithNullVariables() {
    EmailData emailData = EmailData.builder()
        .to("user@example.com")
        .emailType(EmailType.NEW_CHAPTERS)
        .variables(null)
        .build();

    assertThat(emailData)
        .isNotNull()
        .hasFieldOrPropertyWithValue("to", "user@example.com")
        .hasFieldOrPropertyWithValue("emailType", EmailType.NEW_CHAPTERS)
        .hasFieldOrPropertyWithValue("variables", null);
  }

  @Test
  void shouldBuildEmailDataWithPartialFields() {
    EmailData emailData = EmailData.builder()
        .to("admin@example.com")
        .build();

    assertThat(emailData)
        .isNotNull()
        .hasFieldOrPropertyWithValue("to", "admin@example.com")
        .hasFieldOrPropertyWithValue("emailType", null)
        .hasFieldOrPropertyWithValue("variables", null);
  }

  @Test
  void shouldAllowSettingEmailDataFields() {
    Map<String, Object> variables = Map.of("key", "value");

    EmailData emailData = EmailData.builder()
        .to("test@example.com")
        .emailType(EmailType.SCRAPER_ERROR)
        .variables(variables)
        .build();

    assertThat(emailData.getTo()).isEqualTo("test@example.com");
    assertThat(emailData.getEmailType()).isEqualTo(EmailType.SCRAPER_ERROR);
    assertThat(emailData.getVariables()).isEqualTo(variables);
  }

  @Test
  void shouldHaveEqualityBasedOnFieldValues() {
    Map<String, Object> variables = Map.of("key", "value");

    EmailData emailData1 = EmailData.builder()
        .to("test@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(variables)
        .build();

    EmailData emailData2 = EmailData.builder()
        .to("test@example.com")
        .emailType(EmailType.WORK_ADDED)
        .variables(variables)
        .build();

    assertThat(emailData1).isEqualTo(emailData2);
  }

  @Test
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    EmailData emailData1 = EmailData.builder()
        .to("test1@example.com")
        .emailType(EmailType.WORK_ADDED)
        .build();

    EmailData emailData2 = EmailData.builder()
        .to("test2@example.com")
        .emailType(EmailType.NEW_CHAPTERS)
        .build();

    assertThat(emailData1.hashCode()).isNotEqualTo(emailData2.hashCode());
  }
}
