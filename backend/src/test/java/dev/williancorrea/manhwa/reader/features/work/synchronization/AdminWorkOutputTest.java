package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AdminWorkOutput")
class AdminWorkOutputTest {

  private Work testWork;
  private String storageBase;
  private UUID workId;
  private UUID syncId1;
  private UUID syncId2;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    syncId1 = UUID.randomUUID();
    syncId2 = UUID.randomUUID();
    storageBase = "https://storage.example.com/bucket";
  }

  @Nested
  @DisplayName("fromEntity() with complete work data")
  class FromEntityCompleteTests {

    @BeforeEach
    void setUp() {
      var title1 = WorkTitle.builder()
          .id(UUID.randomUUID())
          .title("Official Title")
          .isOfficial(true)
          .build();
      var title2 = WorkTitle.builder()
          .id(UUID.randomUUID())
          .title("Alt Title")
          .isOfficial(false)
          .build();

      var sync1 = WorkSynchronization.builder()
          .id(syncId1)
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("ext-123")
          .build();
      var sync2 = WorkSynchronization.builder()
          .id(syncId2)
          .origin(SynchronizationOriginType.LYCANTOONS)
          .externalId("ext-456")
          .build();

      testWork = Work.builder()
          .id(workId)
          .slug("test-work-slug")
          .titles(List.of(title1, title2))
          .status(WorkStatus.ONGOING)
          .type(WorkType.MANGA)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .synchronizations(List.of(sync1, sync2))
          .build();
    }

    @Test
    @DisplayName("should create output with all fields populated")
    void shouldCreateOutputWithAllFieldsPopulated() {
      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.id()).isEqualTo(workId);
      assertThat(output.title()).isEqualTo("Official Title");
      assertThat(output.slug()).isEqualTo("test-work-slug");
      assertThat(output.status()).isEqualTo("ONGOING");
      assertThat(output.type()).isEqualTo("MANGA");
      assertThat(output.publicationDemographic()).isEqualTo("SHOUNEN");
      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should extract official title first")
    void shouldExtractOfficialTitleFirst() {
      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.title()).isEqualTo("Official Title");
    }

    @Test
    @DisplayName("should include all titles")
    void shouldIncludeAllTitles() {
      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.titles())
          .hasSize(2)
          .contains("Official Title", "Alt Title");
    }

    @Test
    @DisplayName("should extract synchronization origins")
    void shouldExtractSynchronizationOrigins() {
      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.synchronizationOrigins())
          .hasSize(2)
          .contains("MANGADEX", "LYCANTOONS");
    }

    @Test
    @DisplayName("should extract synchronization details")
    void shouldExtractSynchronizationDetails() {
      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.synchronizations())
          .hasSize(2);
      assertThat(output.synchronizations())
          .anySatisfy(s -> assertThat(s.origin()).isEqualTo("MANGADEX")
              .as("Should have MANGADEX sync"))
          .anySatisfy(s -> assertThat(s.origin()).isEqualTo("LYCANTOONS")
              .as("Should have LYCANTOONS sync"));
    }
  }

  @Nested
  @DisplayName("fromEntity() with minimal work data")
  class FromEntityMinimalTests {

    @BeforeEach
    void setUp() {
      testWork = Work.builder()
          .id(workId)
          .slug("minimal-work")
          .build();
    }

    @Test
    @DisplayName("should handle null titles list")
    void shouldHandleNullTitlesList() {
      testWork.setTitles(null);

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.title()).isNull();
      assertThat(output.titles()).isEmpty();
    }

    @Test
    @DisplayName("should handle empty titles list")
    void shouldHandleEmptyTitlesList() {
      testWork.setTitles(new ArrayList<>());

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.title()).isNull();
      assertThat(output.titles()).isEmpty();
    }

    @Test
    @DisplayName("should handle null synchronizations")
    void shouldHandleNullSynchronizations() {
      testWork.setSynchronizations(null);

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.synchronizationOrigins()).isEmpty();
      assertThat(output.synchronizations()).isEmpty();
    }

    @Test
    @DisplayName("should handle empty synchronizations")
    void shouldHandleEmptySynchronizations() {
      testWork.setSynchronizations(new ArrayList<>());

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.synchronizationOrigins()).isEmpty();
      assertThat(output.synchronizations()).isEmpty();
    }

    @Test
    @DisplayName("should handle null covers list")
    void shouldHandleNullCoverUrl() {
      testWork.setCovers(null);

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle null status and type")
    void shouldHandleNullStatusAndType() {
      testWork.setStatus(null);
      testWork.setType(null);
      testWork.setPublicationDemographic(null);

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.status()).isNull();
      assertThat(output.type()).isNull();
      assertThat(output.publicationDemographic()).isNull();
    }
  }

  @Nested
  @DisplayName("fromEntity() with title variations")
  class FromEntityTitleVariationsTests {

    @Test
    @DisplayName("should use first title when no official title exists")
    void shouldUseFirstTitleWhenNoOfficialTitleExists() {
      var title1 = WorkTitle.builder()
          .title("First Title")
          .isOfficial(false)
          .build();
      var title2 = WorkTitle.builder()
          .title("Second Title")
          .isOfficial(false)
          .build();

      testWork = Work.builder()
          .id(workId)
          .slug("test")
          .titles(List.of(title1, title2))
          .build();

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.title()).isEqualTo("First Title");
    }

    @Test
    @DisplayName("should filter out blank titles")
    void shouldFilterOutBlankTitles() {
      var title1 = WorkTitle.builder()
          .title("Valid Title")
          .isOfficial(true)
          .build();
      var title2 = WorkTitle.builder()
          .title("   ")
          .isOfficial(false)
          .build();
      var title3 = WorkTitle.builder()
          .title("")
          .isOfficial(false)
          .build();

      testWork = Work.builder()
          .id(workId)
          .slug("test")
          .titles(List.of(title1, title2, title3))
          .build();

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.titles())
          .hasSize(1)
          .contains("Valid Title");
    }

    @Test
    @DisplayName("should remove duplicate titles")
    void shouldRemoveDuplicateTitles() {
      var title1 = WorkTitle.builder()
          .title("Title")
          .isOfficial(true)
          .build();
      var title2 = WorkTitle.builder()
          .title("Title")
          .isOfficial(false)
          .build();

      testWork = Work.builder()
          .id(workId)
          .slug("test")
          .titles(List.of(title1, title2))
          .build();

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.titles())
          .hasSize(1)
          .contains("Title");
    }

    @Test
    @DisplayName("should handle null title values in list")
    void shouldHandleNullTitleValuesInList() {
      var title1 = WorkTitle.builder()
          .title("Valid Title")
          .isOfficial(true)
          .build();
      var title2 = WorkTitle.builder()
          .title(null)
          .isOfficial(false)
          .build();

      testWork = Work.builder()
          .id(workId)
          .slug("test")
          .titles(List.of(title1, title2))
          .build();

      var output = AdminWorkOutput.fromEntity(testWork, storageBase);

      assertThat(output.titles())
          .hasSize(1)
          .contains("Valid Title");
    }
  }

  @Nested
  @DisplayName("SynchronizationDetail record")
  class SynchronizationDetailTests {

    @Test
    @DisplayName("should create detail with origin and externalId")
    void shouldCreateDetailWithOriginAndExternalId() {
      var detail = new AdminWorkOutput.SynchronizationDetail("MANGADEX", "ext-123");

      assertThat(detail.origin()).isEqualTo("MANGADEX");
      assertThat(detail.externalId()).isEqualTo("ext-123");
    }

    @Test
    @DisplayName("should have equals method")
    void shouldHaveEqualsMethod() {
      var detail1 = new AdminWorkOutput.SynchronizationDetail("MANGADEX", "ext-123");
      var detail2 = new AdminWorkOutput.SynchronizationDetail("MANGADEX", "ext-123");

      assertThat(detail1).isEqualTo(detail2);
    }

    @Test
    @DisplayName("should have hashCode method")
    void shouldHaveHashCodeMethod() {
      var detail1 = new AdminWorkOutput.SynchronizationDetail("MANGADEX", "ext-123");
      var detail2 = new AdminWorkOutput.SynchronizationDetail("MANGADEX", "ext-123");

      assertThat(detail1.hashCode()).isEqualTo(detail2.hashCode());
    }
  }

  @Nested
  @DisplayName("coverUrl conditional logic")
  class CoverUrlConditionalTests {

    @Test
    @DisplayName("should build full cover URL when getCoverUrl() returns non-null value")
    void shouldBuildFullCoverUrlWhenGetCoverUrlReturnsNonNull() {
      var mockWork = org.mockito.Mockito.mock(Work.class);
      mockWork.setId(workId);
      mockWork.setSlug("test");
      mockWork.setTitles(new ArrayList<>());
      mockWork.setSynchronizations(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getId()).thenReturn(workId);
      org.mockito.Mockito.when(mockWork.getSlug()).thenReturn("test");
      org.mockito.Mockito.when(mockWork.getTitles()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getSynchronizations()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getStatus()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getType()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getPublicationDemographic()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getCoverUrl()).thenReturn("/covers/123.jpg");

      var output = AdminWorkOutput.fromEntity(mockWork, storageBase);

      assertThat(output.coverUrl())
          .isEqualTo(storageBase + "/covers/123.jpg");
    }

    @Test
    @DisplayName("should return null coverUrl when getCoverUrl() returns null")
    void shouldReturnNullCoverUrlWhenGetCoverUrlReturnsNull() {
      var mockWork = org.mockito.Mockito.mock(Work.class);
      mockWork.setId(workId);
      mockWork.setSlug("test");
      mockWork.setTitles(new ArrayList<>());
      mockWork.setSynchronizations(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getId()).thenReturn(workId);
      org.mockito.Mockito.when(mockWork.getSlug()).thenReturn("test");
      org.mockito.Mockito.when(mockWork.getTitles()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getSynchronizations()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getStatus()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getType()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getPublicationDemographic()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getCoverUrl()).thenReturn(null);

      var output = AdminWorkOutput.fromEntity(mockWork, storageBase);

      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should concatenate storageBase correctly with cover path")
    void shouldConcatenateStorageBaseCorrectlyWithCoverPath() {
      var mockWork = org.mockito.Mockito.mock(Work.class);
      mockWork.setId(workId);
      mockWork.setSlug("test");
      mockWork.setTitles(new ArrayList<>());
      mockWork.setSynchronizations(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getId()).thenReturn(workId);
      org.mockito.Mockito.when(mockWork.getSlug()).thenReturn("test");
      org.mockito.Mockito.when(mockWork.getTitles()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getSynchronizations()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getStatus()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getType()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getPublicationDemographic()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getCoverUrl()).thenReturn("/path/to/cover.png");

      var output = AdminWorkOutput.fromEntity(mockWork, "https://storage.com/bucket");

      assertThat(output.coverUrl())
          .isEqualTo("https://storage.com/bucket/path/to/cover.png");
    }

    @Test
    @DisplayName("should use storageBase from parameter in coverUrl construction")
    void shouldUseStorageBaseFromParameterInCoverUrlConstruction() {
      var mockWork = org.mockito.Mockito.mock(Work.class);
      mockWork.setId(workId);
      mockWork.setSlug("test");
      mockWork.setTitles(new ArrayList<>());
      mockWork.setSynchronizations(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getId()).thenReturn(workId);
      org.mockito.Mockito.when(mockWork.getSlug()).thenReturn("test");
      org.mockito.Mockito.when(mockWork.getTitles()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getSynchronizations()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getStatus()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getType()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getPublicationDemographic()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getCoverUrl()).thenReturn("/cover.jpg");

      var differentStorageBase = "https://cdn.example.com/media";
      var output = AdminWorkOutput.fromEntity(mockWork, differentStorageBase);

      assertThat(output.coverUrl())
          .startsWith(differentStorageBase)
          .isEqualTo(differentStorageBase + "/cover.jpg");
    }

    @Test
    @DisplayName("should handle empty string coverUrl from getCoverUrl()")
    void shouldHandleEmptyStringCoverUrl() {
      var mockWork = org.mockito.Mockito.mock(Work.class);
      mockWork.setId(workId);
      mockWork.setSlug("test");
      mockWork.setTitles(new ArrayList<>());
      mockWork.setSynchronizations(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getId()).thenReturn(workId);
      org.mockito.Mockito.when(mockWork.getSlug()).thenReturn("test");
      org.mockito.Mockito.when(mockWork.getTitles()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getSynchronizations()).thenReturn(new ArrayList<>());
      org.mockito.Mockito.when(mockWork.getStatus()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getType()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getPublicationDemographic()).thenReturn(null);
      org.mockito.Mockito.when(mockWork.getCoverUrl()).thenReturn("");

      var output = AdminWorkOutput.fromEntity(mockWork, storageBase);

      assertThat(output.coverUrl())
          .isEqualTo(storageBase);
    }
  }

  @Nested
  @DisplayName("storage URL handling")
  class StorageUrlHandlingTests {

    @Test
    @DisplayName("should handle different storage bases")
    void shouldHandleDifferentStorageBases() {
      testWork = Work.builder()
          .id(workId)
          .slug("test")
          .build();

      var output = AdminWorkOutput.fromEntity(testWork, "https://different-storage.com/bucket");

      assertThat(output).isNotNull();
    }
  }
}
