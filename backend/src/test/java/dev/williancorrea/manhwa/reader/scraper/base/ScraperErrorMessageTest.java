package dev.williancorrea.manhwa.reader.scraper.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ScraperErrorMessage")
class ScraperErrorMessageTest {

  @Nested
  @DisplayName("Work validation messages")
  class WorkValidationMessages {

    @Test
    void shouldHaveWorkIsNullMessage() {
      assertEquals("Work cannot be null", ScraperErrorMessage.VALIDATION_ERROR_WORK_IS_NULL);
    }

    @Test
    void shouldHaveWorkIdIsNullMessage() {
      assertEquals("Work ID cannot be null", ScraperErrorMessage.VALIDATION_ERROR_WORK_ID_IS_NULL);
    }

    @Test
    void shouldHaveWorkTypeIsNullMessage() {
      assertEquals("Work type cannot be null", ScraperErrorMessage.VALIDATION_ERROR_WORK_TYPE_IS_NULL);
    }

    @Test
    void shouldHavePublicationDemographicIsNullMessage() {
      assertEquals("Publication demographic cannot be null",
          ScraperErrorMessage.VALIDATION_ERROR_WORK_PUBLICATION_DEMOGRAPHIC_IS_NULL);
    }

    @Test
    void shouldHaveWorkSlugIsNullMessage() {
      assertEquals("Work slug cannot be null", ScraperErrorMessage.VALIDATION_ERROR_WORK_SLUG_IS_NULL);
    }

    @Test
    void shouldHaveWorkSlugIsBlankMessage() {
      assertEquals("Work slug cannot be null", ScraperErrorMessage.VALIDATION_ERROR_WORK_SLUG_IS_BLANK);
    }
  }

  @Nested
  @DisplayName("Time-related validation messages")
  class TimeValidationMessages {

    @Test
    void shouldHaveCreateAtIsNullMessage() {
      assertEquals("Create at cannot be null", ScraperErrorMessage.VALIDATION_ERROR_CREATE_AT_IS_NULL);
    }

    @Test
    void shouldHaveUpdateAtIsNullMessage() {
      assertEquals("Update at cannot be null", ScraperErrorMessage.VALIDATION_ERROR_UPDATE_AT_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Title validation messages")
  class TitleValidationMessages {

    @Test
    void shouldHaveTitleIsBlankMessage() {
      assertEquals("Title cannot be blank", ScraperErrorMessage.VALIDATION_ERROR_TITLE_IS_BLANK);
    }

    @Test
    void shouldHaveTitleIsNullMessage() {
      assertEquals("Title cannot be null", ScraperErrorMessage.VALIDATION_ERROR_TITLE_IS_NULL);
    }

    @Test
    void shouldHaveTitleListIsNullMessage() {
      assertEquals("Titles cannot be null", ScraperErrorMessage.VALIDATION_ERROR_TITLE_LIST_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Synopsis validation messages")
  class SynopsisValidationMessages {

    @Test
    void shouldHaveSynopsesIsBlankMessage() {
      assertEquals("Synopses cannot be empty", ScraperErrorMessage.VALIDATION_ERROR_SYNOPSES_IS_BLANK);
    }

    @Test
    void shouldHaveSynopsesListIsNullMessage() {
      assertEquals("Synopses cannot be null", ScraperErrorMessage.VALIDATION_ERROR_SYNOPSES_LIST_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Synchronization validation messages")
  class SynchronizationValidationMessages {

    @Test
    void shouldHaveSynchronizationOriginIsNullMessage() {
      assertEquals("Synchronization origin cannot be null",
          ScraperErrorMessage.VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
    }
  }

  @Nested
  @DisplayName("External work validation messages")
  class ExternalWorkValidationMessages {

    @Test
    void shouldHaveExternalWorkIsNullMessage() {
      assertEquals("External work cannot be null",
          ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL);
    }

    @Test
    void shouldHaveExternalWorkIdIsNullMessage() {
      assertEquals("External work ID cannot be null",
          ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL);
    }

    @Test
    void shouldHaveExternalWorkIdIsBlankMessage() {
      assertEquals("External work ID cannot be blank",
          ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_BLANK);
    }

    @Test
    void shouldHaveExternalWorkNameIsNullMessage() {
      assertEquals("External work name cannot be null",
          ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_NAME_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Site validation messages")
  class SiteValidationMessages {

    @Test
    void shouldHaveSiteTypeIsNullMessage() {
      assertEquals("Site type cannot be null", ScraperErrorMessage.VALIDATION_ERROR_SITE_TYPE_IS_NULL);
    }

    @Test
    void shouldHaveSiteLinkIsBlankMessage() {
      assertEquals("Site link cannot be empty", ScraperErrorMessage.VALIDATION_ERROR_SITE_LINK_IS_BLANK);
    }

    @Test
    void shouldHaveSiteLinkIsNullMessage() {
      assertEquals("Site link cannot be null", ScraperErrorMessage.VALIDATION_ERROR_SITE_LINK_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Scanlator validation messages")
  class ScanlatorValidationMessages {

    @Test
    void shouldHaveScanlatorIsNullMessage() {
      assertEquals("Scanlator cannot be null", ScraperErrorMessage.VALIDATION_ERROR_SCANLATOR_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Tag validation messages")
  class TagValidationMessages {

    @Test
    void shouldHaveTagListIsNullMessage() {
      assertEquals("Tag list cannot be null", ScraperErrorMessage.VALIDATION_ERROR_TAG_LIST_IS_NULL);
    }

    @Test
    void shouldHaveTagGroupIsNullMessage() {
      assertEquals("Tag group cannot be null", ScraperErrorMessage.VALIDATION_ERROR_TAG_GROUP_IS_NULL);
    }

    @Test
    void shouldHaveTagNameIsBlankMessage() {
      assertEquals("Tag name cannot be blank", ScraperErrorMessage.VALIDATION_ERROR_TAG_NAME_IS_BLANK);
    }

    @Test
    void shouldHaveTagNameIsNullMessage() {
      assertEquals("Tag name cannot be null", ScraperErrorMessage.VALIDATION_ERROR_TAG_NAME_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Author validation messages")
  class AuthorValidationMessages {

    @Test
    void shouldHaveAuthorListIsNullMessage() {
      assertEquals("Author list cannot be null", ScraperErrorMessage.VALIDATION_ERROR_AUTHOR_LIST_IS_NULL);
    }
  }

  @Nested
  @DisplayName("General validation messages")
  class GeneralValidationMessages {

    @Test
    void shouldHaveMessageIsNullMessage() {
      assertEquals("Message cannot be null", ScraperErrorMessage.VALIDATION_ERROR_MESSAGE_IS_NULL);
    }
  }

  @Nested
  @DisplayName("Constructor validation messages")
  class ConstructorValidationMessages {

    @Test
    void shouldHaveConstructorWorkServiceIsNullMessage() {
      assertEquals("Work service cannot be null",
          ScraperErrorMessage.CONSTRUCTOR_WORK_SERVICE_IS_NULL);
    }

    @Test
    void shouldHaveConstructorLanguageServiceIsNullMessage() {
      assertEquals("Language service cannot be null",
          ScraperErrorMessage.CONSTRUCTOR_LANGUAGE_SERVICE_IS_NULL);
    }

    @Test
    void shouldHaveConstructorScanlatorServiceIsNullMessage() {
      assertEquals("Scanlator service cannot be null",
          ScraperErrorMessage.CONSTRUCTOR_SCANLATOR_SERVICE_IS_NULL);
    }

    @Test
    void shouldHaveConstructorScanlatorSynchronizationErrorServiceIsNullMessage() {
      assertEquals("Scanlator synchronization error service cannot be null",
          ScraperErrorMessage.CONSTRUCTOR_SCANLATOR_SYNCHRONIZATION_ERROR_SERVICE_IS_NULL);
    }

    @Test
    void shouldHaveConstructorWorkLinkRepositoryIsNullMessage() {
      assertEquals("Work link repository cannot be null",
          ScraperErrorMessage.CONSTRUCTOR_WORK_LINK_REPOSITORY_IS_NULL);
    }
  }

  @Nested
  @DisplayName("All messages are not null")
  class AllMessagesAreNotNull {

    @Test
    void shouldAllMessagesNotBeNull() {
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_WORK_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_TITLE_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_TITLE_LIST_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_SYNOPSES_LIST_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_TAG_LIST_IS_NULL);
      assertNotNull(ScraperErrorMessage.VALIDATION_ERROR_AUTHOR_LIST_IS_NULL);
    }
  }
}
