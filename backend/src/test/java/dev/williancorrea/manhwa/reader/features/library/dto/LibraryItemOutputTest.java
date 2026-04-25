package dev.williancorrea.manhwa.reader.features.library.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LibraryItemOutput")
class LibraryItemOutputTest {

  private UUID workId;
  private String slug;
  private String title;
  private String coverUrl;
  private String publicationDemographic;
  private String workStatus;
  private Long chapterCount;
  private String libraryStatus;
  private Long unreadCount;
  private String originalLanguageCode;
  private String originalLanguageFlag;
  private String originalLanguageName;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    slug = "test-slug";
    title = "Test Title";
    coverUrl = "https://example.com/cover.jpg";
    publicationDemographic = "SHOUNEN";
    workStatus = "ONGOING";
    chapterCount = 10L;
    libraryStatus = "READING";
    unreadCount = 5L;
    originalLanguageCode = "ja";
    originalLanguageFlag = "🇯🇵";
    originalLanguageName = "Japanese";
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create record with all fields")
    void shouldCreateRecordWithAllFields() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output)
          .isNotNull()
          .satisfies(o -> {
            assertThat(o.workId()).isEqualTo(workId);
            assertThat(o.slug()).isEqualTo(slug);
            assertThat(o.title()).isEqualTo(title);
            assertThat(o.coverUrl()).isEqualTo(coverUrl);
            assertThat(o.publicationDemographic()).isEqualTo(publicationDemographic);
            assertThat(o.workStatus()).isEqualTo(workStatus);
            assertThat(o.chapterCount()).isEqualTo(chapterCount);
            assertThat(o.libraryStatus()).isEqualTo(libraryStatus);
            assertThat(o.unreadCount()).isEqualTo(unreadCount);
            assertThat(o.originalLanguageCode()).isEqualTo(originalLanguageCode);
            assertThat(o.originalLanguageFlag()).isEqualTo(originalLanguageFlag);
            assertThat(o.originalLanguageName()).isEqualTo(originalLanguageName);
          });
    }

    @Test
    @DisplayName("should create record with null fields")
    void shouldCreateRecordWithNullFields() {
      var output = new LibraryItemOutput(
          null, null, null, null, null, null,
          null, null, null, null, null, null
      );

      assertThat(output)
          .isNotNull()
          .satisfies(o -> {
            assertThat(o.workId()).isNull();
            assertThat(o.slug()).isNull();
            assertThat(o.title()).isNull();
            assertThat(o.coverUrl()).isNull();
            assertThat(o.publicationDemographic()).isNull();
            assertThat(o.workStatus()).isNull();
            assertThat(o.chapterCount()).isNull();
            assertThat(o.libraryStatus()).isNull();
            assertThat(o.unreadCount()).isNull();
            assertThat(o.originalLanguageCode()).isNull();
            assertThat(o.originalLanguageFlag()).isNull();
            assertThat(o.originalLanguageName()).isNull();
          });
    }
  }

  @Nested
  @DisplayName("Accessors")
  class AccessorTests {

    @Test
    @DisplayName("should return workId")
    void shouldReturnWorkId() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.workId()).isEqualTo(workId);
    }

    @Test
    @DisplayName("should return slug")
    void shouldReturnSlug() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.slug()).isEqualTo(slug);
    }

    @Test
    @DisplayName("should return title")
    void shouldReturnTitle() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.title()).isEqualTo(title);
    }

    @Test
    @DisplayName("should return coverUrl")
    void shouldReturnCoverUrl() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.coverUrl()).isEqualTo(coverUrl);
    }

    @Test
    @DisplayName("should return publicationDemographic")
    void shouldReturnPublicationDemographic() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.publicationDemographic()).isEqualTo(publicationDemographic);
    }

    @Test
    @DisplayName("should return workStatus")
    void shouldReturnWorkStatus() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.workStatus()).isEqualTo(workStatus);
    }

    @Test
    @DisplayName("should return chapterCount")
    void shouldReturnChapterCount() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.chapterCount()).isEqualTo(chapterCount);
    }

    @Test
    @DisplayName("should return libraryStatus")
    void shouldReturnLibraryStatus() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.libraryStatus()).isEqualTo(libraryStatus);
    }

    @Test
    @DisplayName("should return unreadCount")
    void shouldReturnUnreadCount() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.unreadCount()).isEqualTo(unreadCount);
    }

    @Test
    @DisplayName("should return originalLanguageCode")
    void shouldReturnOriginalLanguageCode() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.originalLanguageCode()).isEqualTo(originalLanguageCode);
    }

    @Test
    @DisplayName("should return originalLanguageFlag")
    void shouldReturnOriginalLanguageFlag() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.originalLanguageFlag()).isEqualTo(originalLanguageFlag);
    }

    @Test
    @DisplayName("should return originalLanguageName")
    void shouldReturnOriginalLanguageName() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.originalLanguageName()).isEqualTo(originalLanguageName);
    }
  }

  @Nested
  @DisplayName("Equality")
  class EqualityTests {

    @Test
    @DisplayName("should be equal when all fields match")
    void shouldBeEqualWhenAllFieldsMatch() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1).isEqualTo(output2);
    }

    @Test
    @DisplayName("should not be equal when workId differs")
    void shouldNotBeEqualWhenWorkIdDiffers() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          UUID.randomUUID(), slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1).isNotEqualTo(output2);
    }

    @Test
    @DisplayName("should not be equal when slug differs")
    void shouldNotBeEqualWhenSlugDiffers() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          workId, "different-slug", title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1).isNotEqualTo(output2);
    }

    @Test
    @DisplayName("should not be equal when chapterCount differs")
    void shouldNotBeEqualWhenChapterCountDiffers() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          20L, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1).isNotEqualTo(output2);
    }
  }

  @Nested
  @DisplayName("Hash Code")
  class HashCodeTests {

    @Test
    @DisplayName("should have same hash code when equal")
    void shouldHaveSameHashCodeWhenEqual() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1.hashCode()).isEqualTo(output2.hashCode());
    }

    @Test
    @DisplayName("should have different hash code when fields differ")
    void shouldHaveDifferentHashCodeWhenFieldsDiffer() {
      var output1 = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var output2 = new LibraryItemOutput(
          UUID.randomUUID(), slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output1.hashCode()).isNotEqualTo(output2.hashCode());
    }
  }

  @Nested
  @DisplayName("ToString")
  class ToStringTests {

    @Test
    @DisplayName("should contain record name")
    void shouldContainRecordName() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.toString()).contains("LibraryItemOutput");
    }

    @Test
    @DisplayName("should contain field values in toString")
    void shouldContainFieldValuesInToString() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      var str = output.toString();
      assertThat(str).contains(workId.toString());
      assertThat(str).contains(slug);
      assertThat(str).contains(title);
    }
  }

  @Nested
  @DisplayName("Special Cases")
  class SpecialCasesTests {

    @Test
    @DisplayName("should handle zero chapter count")
    void shouldHandleZeroChapterCount() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          0L, libraryStatus, unreadCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.chapterCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should handle zero unread count")
    void shouldHandleZeroUnreadCount() {
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          chapterCount, libraryStatus, 0L, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.unreadCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should handle empty strings")
    void shouldHandleEmptyStrings() {
      var output = new LibraryItemOutput(
          workId, "", "", "", "", "",
          chapterCount, "", unreadCount, "",
          "", ""
      );

      assertThat(output.slug()).isEmpty();
      assertThat(output.title()).isEmpty();
      assertThat(output.coverUrl()).isEmpty();
    }

    @Test
    @DisplayName("should handle large numbers")
    void shouldHandleLargeNumbers() {
      var largeCount = Long.MAX_VALUE;
      var output = new LibraryItemOutput(
          workId, slug, title, coverUrl, publicationDemographic, workStatus,
          largeCount, libraryStatus, largeCount, originalLanguageCode,
          originalLanguageFlag, originalLanguageName
      );

      assertThat(output.chapterCount()).isEqualTo(largeCount);
      assertThat(output.unreadCount()).isEqualTo(largeCount);
    }

    @Test
    @DisplayName("should handle unicode characters")
    void shouldHandleUnicodeCharacters() {
      var output = new LibraryItemOutput(
          workId, "日本-スラッグ", "日本のタイトル", coverUrl,
          publicationDemographic, workStatus, chapterCount, libraryStatus,
          unreadCount, "ja", "🇯🇵", "日本語"
      );

      assertThat(output.slug()).contains("日本");
      assertThat(output.title()).contains("日本");
      assertThat(output.originalLanguageName()).contains("日本");
    }
  }
}
