package dev.williancorrea.manhwa.reader.scraper.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import dev.williancorrea.manhwa.reader.features.work.Work;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Scraper Interface")
class ScraperTest {

  @Nested
  @DisplayName("Contract validation")
  class ContractValidationTests {

    @Test
    void shouldHaveScheduledSynchronizationMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).ScheduledSynchronization();
      scraper.ScheduledSynchronization();

      verify(scraper).ScheduledSynchronization();
    }

    @Test
    void shouldHaveSynchronizeByExternalIdMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).synchronizeByExternalId(anyString());
      scraper.synchronizeByExternalId("123");

      verify(scraper).synchronizeByExternalId("123");
    }

    @Test
    void shouldHaveSynchronizeByExternalIdWithPaginationMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).synchronizeByExternalId(any(), anyLong(), anyLong());
      scraper.synchronizeByExternalId(new Object(), 1L, 10L);

      verify(scraper).synchronizeByExternalId(any(), anyLong(), anyLong());
    }

    @Test
    void shouldHaveSynchronizeByWorkMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).synchronizeByWork(any());
      scraper.synchronizeByWork(mock(Work.class));

      verify(scraper).synchronizeByWork(any());
    }
  }

  @Nested
  @DisplayName("Preparation methods")
  class PreparationMethodsTests {

    @Test
    void shouldHavePrepareSyncTitleMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncTitle(any(), any());
      scraper.prepareSyncTitle(mock(Work.class), new Object());

      verify(scraper).prepareSyncTitle(any(), any());
    }

    @Test
    void shouldHavePrepareSyncAttributesMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncAttributes(any(), any());
      scraper.prepareSyncAttributes(mock(Work.class), new Object());

      verify(scraper).prepareSyncAttributes(any(), any());
    }

    @Test
    void shouldHavePrepareSynchronizationMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSynchronization(any(), any());
      scraper.prepareSynchronization(mock(Work.class), new Object());

      verify(scraper).prepareSynchronization(any(), any());
    }

    @Test
    void shouldHavePrepareSyncSynopsesMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncSynopses(any(), any());
      scraper.prepareSyncSynopses(mock(Work.class), new Object());

      verify(scraper).prepareSyncSynopses(any(), any());
    }

    @Test
    void shouldHavePrepareSyncLinksMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncLinks(any(), any());
      scraper.prepareSyncLinks(mock(Work.class), new Object());

      verify(scraper).prepareSyncLinks(any(), any());
    }

    @Test
    void shouldHavePrepareSyncTagsMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncTags(any(), any());
      scraper.prepareSyncTags(mock(Work.class), new Object());

      verify(scraper).prepareSyncTags(any(), any());
    }

    @Test
    void shouldHavePrepareSyncAuthorsMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncAuthors(any(), any());
      scraper.prepareSyncAuthors(mock(Work.class), new Object());

      verify(scraper).prepareSyncAuthors(any(), any());
    }

    @Test
    void shouldHavePrepareSyncCoverMethod() throws IOException, InterruptedException {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncCover(any(), any());
      scraper.prepareSyncCover(mock(Work.class), new Object());

      verify(scraper).prepareSyncCover(any(), any());
    }

    @Test
    void shouldHavePrepareSyncRelationshipsMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncRelationships(any(), any());
      scraper.prepareSyncRelationships(mock(Work.class), new Object());

      verify(scraper).prepareSyncRelationships(any(), any());
    }

    @Test
    void shouldHavePrepareSyncChaptersMethod() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);

      doNothing().when(scraper).prepareSyncChapters(any(), any(), anyLong(), anyLong());
      scraper.prepareSyncChapters(mock(Work.class), new Object(), 1L, 10L);

      verify(scraper).prepareSyncChapters(any(), any(), anyLong(), anyLong());
    }
  }

  @Nested
  @DisplayName("Generic type parameter")
  class GenericTypeParameterTests {

    @Test
    void shouldBeGenericInterface() {
      var scraper = mock(Scraper.class);
      assertNotNull(scraper);
    }

    @Test
    void shouldAcceptAnyTypeParameter() {
      var scraperWithObject = mock(Scraper.class);
      var scraperWithString = mock(Scraper.class);
      var scraperWithLong = mock(Scraper.class);

      assertNotNull(scraperWithObject);
      assertNotNull(scraperWithString);
      assertNotNull(scraperWithLong);
    }
  }

  @Nested
  @DisplayName("Method signatures validation")
  class MethodSignaturesValidationTests {

    @Test
    void shouldValidateScheduledSynchronizationSignature() {
      var scraper = mock(Scraper.class);
      doNothing().when(scraper).ScheduledSynchronization();

      scraper.ScheduledSynchronization();

      verify(scraper).ScheduledSynchronization();
    }

    @Test
    void shouldValidateSingleParameterSynchronizeByExternalId() {
      var scraper = mock(Scraper.class);
      doNothing().when(scraper).synchronizeByExternalId(anyString());

      scraper.synchronizeByExternalId("test-id");

      verify(scraper).synchronizeByExternalId("test-id");
    }

    @Test
    void shouldValidateThreeParameterSynchronizeByExternalId() {
      var scraper = mock(Scraper.class);
      doNothing().when(scraper).synchronizeByExternalId(any(), anyLong(), anyLong());

      var dto = new Object();
      scraper.synchronizeByExternalId(dto, 1L, 10L);

      verify(scraper).synchronizeByExternalId(dto, 1L, 10L);
    }

    @Test
    void shouldValidateSynchronizeByWorkWithWorkParameter() {
      var scraper = mock(Scraper.class);
      var work = mock(Work.class);
      doNothing().when(scraper).synchronizeByWork(work);

      scraper.synchronizeByWork(work);

      verify(scraper).synchronizeByWork(work);
    }

    @Test
    void shouldValidatePrepareSyncTitleWithTwoParameters() {
      var scraper = mock(Scraper.class);
      var work = mock(Work.class);
      var dto = new Object();
      doNothing().when(scraper).prepareSyncTitle(work, dto);

      scraper.prepareSyncTitle(work, dto);

      verify(scraper).prepareSyncTitle(work, dto);
    }

    @Test
    void shouldValidatePrepareSyncCoverThrowsExceptions() throws IOException, InterruptedException {
      var scraper = mock(Scraper.class);
      var work = mock(Work.class);
      var dto = new Object();

      doNothing().when(scraper).prepareSyncCover(work, dto);
      scraper.prepareSyncCover(work, dto);

      verify(scraper).prepareSyncCover(work, dto);
    }

    @Test
    void shouldValidatePrepareSyncChaptersWithFourParameters() {
      var scraper = mock(Scraper.class);
      var work = mock(Work.class);
      var dto = new Object();
      doNothing().when(scraper).prepareSyncChapters(work, dto, 1L, 5L);

      scraper.prepareSyncChapters(work, dto, 1L, 5L);

      verify(scraper).prepareSyncChapters(work, dto, 1L, 5L);
    }
  }

  @Nested
  @DisplayName("Implementation contract")
  class ImplementationContractTests {

    @Test
    void shouldAllowMultipleImplementations() {
      var scraper1 = mock(Scraper.class);
      var scraper2 = mock(Scraper.class);

      assertNotNull(scraper1);
      assertNotNull(scraper2);
    }

    @Test
    void shouldEnforceAllMethodsImplementation() throws IOException, InterruptedException {
      var scraper = mock(Scraper.class);

      doNothing().when(scraper).ScheduledSynchronization();
      doNothing().when(scraper).synchronizeByExternalId(anyString());
      doNothing().when(scraper).synchronizeByExternalId(any(), anyLong(), anyLong());
      doNothing().when(scraper).synchronizeByWork(any());
      doNothing().when(scraper).prepareSyncTitle(any(), any());
      doNothing().when(scraper).prepareSyncAttributes(any(), any());
      doNothing().when(scraper).prepareSynchronization(any(), any());
      doNothing().when(scraper).prepareSyncSynopses(any(), any());
      doNothing().when(scraper).prepareSyncLinks(any(), any());
      doNothing().when(scraper).prepareSyncTags(any(), any());
      doNothing().when(scraper).prepareSyncAuthors(any(), any());
      doNothing().when(scraper).prepareSyncCover(any(), any());
      doNothing().when(scraper).prepareSyncRelationships(any(), any());
      doNothing().when(scraper).prepareSyncChapters(any(), any(), anyLong(), anyLong());

      scraper.ScheduledSynchronization();
      scraper.synchronizeByExternalId("123");
      scraper.synchronizeByExternalId(new Object(), 1L, 10L);
      scraper.synchronizeByWork(mock(Work.class));
      scraper.prepareSyncTitle(mock(Work.class), new Object());
      scraper.prepareSyncAttributes(mock(Work.class), new Object());
      scraper.prepareSynchronization(mock(Work.class), new Object());
      scraper.prepareSyncSynopses(mock(Work.class), new Object());
      scraper.prepareSyncLinks(mock(Work.class), new Object());
      scraper.prepareSyncTags(mock(Work.class), new Object());
      scraper.prepareSyncAuthors(mock(Work.class), new Object());
      scraper.prepareSyncCover(mock(Work.class), new Object());
      scraper.prepareSyncRelationships(mock(Work.class), new Object());
      scraper.prepareSyncChapters(mock(Work.class), new Object(), 1L, 10L);

      verify(scraper).ScheduledSynchronization();
      verify(scraper).synchronizeByExternalId(anyString());
      verify(scraper).synchronizeByExternalId(any(), anyLong(), anyLong());
      verify(scraper).synchronizeByWork(any());
      verify(scraper).prepareSyncTitle(any(), any());
      verify(scraper).prepareSyncAttributes(any(), any());
      verify(scraper).prepareSynchronization(any(), any());
      verify(scraper).prepareSyncSynopses(any(), any());
      verify(scraper).prepareSyncLinks(any(), any());
      verify(scraper).prepareSyncTags(any(), any());
      verify(scraper).prepareSyncAuthors(any(), any());
      verify(scraper).prepareSyncCover(any(), any());
      verify(scraper).prepareSyncRelationships(any(), any());
      verify(scraper).prepareSyncChapters(any(), any(), anyLong(), anyLong());
    }
  }
}
