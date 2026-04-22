package dev.williancorrea.manhwa.reader.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTypeTest {

  @Test
  void shouldHaveWorkAddedEmailType() {
    EmailType emailType = EmailType.WORK_ADDED;

    assertThat(emailType)
        .isNotNull()
        .hasFieldOrPropertyWithValue("templateName", "work-added")
        .hasFieldOrPropertyWithValue("subject", "Nova Obra Adicionada");
  }

  @Test
  void shouldHaveNewChaptersEmailType() {
    EmailType emailType = EmailType.NEW_CHAPTERS;

    assertThat(emailType)
        .isNotNull()
        .hasFieldOrPropertyWithValue("templateName", "new-chapters")
        .hasFieldOrPropertyWithValue("subject", "Novos Capítulos Disponíveis");
  }

  @Test
  void shouldHaveScraperErrorEmailType() {
    EmailType emailType = EmailType.SCRAPER_ERROR;

    assertThat(emailType)
        .isNotNull()
        .hasFieldOrPropertyWithValue("templateName", "scraper-error")
        .hasFieldOrPropertyWithValue("subject", "Erro Durante Scraper");
  }

  @Test
  void shouldReturnTemplateName() {
    assertThat(EmailType.WORK_ADDED.getTemplateName()).isEqualTo("work-added");
    assertThat(EmailType.NEW_CHAPTERS.getTemplateName()).isEqualTo("new-chapters");
    assertThat(EmailType.SCRAPER_ERROR.getTemplateName()).isEqualTo("scraper-error");
  }

  @Test
  void shouldReturnSubject() {
    assertThat(EmailType.WORK_ADDED.getSubject()).isEqualTo("Nova Obra Adicionada");
    assertThat(EmailType.NEW_CHAPTERS.getSubject()).isEqualTo("Novos Capítulos Disponíveis");
    assertThat(EmailType.SCRAPER_ERROR.getSubject()).isEqualTo("Erro Durante Scraper");
  }

  @Test
  void shouldHaveAllEnumValues() {
    EmailType[] emailTypes = EmailType.values();

    assertThat(emailTypes)
        .hasSize(3)
        .contains(EmailType.WORK_ADDED, EmailType.NEW_CHAPTERS, EmailType.SCRAPER_ERROR);
  }

  @Test
  void shouldReturnEnumValueByName() {
    assertThat(EmailType.valueOf("WORK_ADDED")).isEqualTo(EmailType.WORK_ADDED);
    assertThat(EmailType.valueOf("NEW_CHAPTERS")).isEqualTo(EmailType.NEW_CHAPTERS);
    assertThat(EmailType.valueOf("SCRAPER_ERROR")).isEqualTo(EmailType.SCRAPER_ERROR);
  }
}
