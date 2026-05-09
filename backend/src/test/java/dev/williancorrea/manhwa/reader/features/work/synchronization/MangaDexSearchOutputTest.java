package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexAttributes;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexData;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexRelationship;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexRelationshipAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MangaDexSearchOutput")
class MangaDexSearchOutputTest {

  private MangaDexData testData;
  private String mangaDexId;

  @BeforeEach
  void setUp() {
    mangaDexId = "manga-12345";
    testData = new MangaDexData();
    testData.setId(mangaDexId);
    testData.setType("manga");
  }

  @Nested
  @DisplayName("fromMangaDexData() with complete data")
  class FromMangaDexDataCompleteTests {

    @BeforeEach
    void setUp() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "English Title", "ja", "Japanese Title"));
      attributes.setAltTitles(List.of(
          Map.of("en", "Alt Title 1"),
          Map.of("ja", "Alt Title 2")
      ));

      var coverArt = new MangaDexRelationship();
      coverArt.setId("cover-id");
      coverArt.setType("cover_art");
      var coverAttrs = new MangaDexRelationshipAttributes();
      coverAttrs.setFileName("cover-image.jpg");
      coverArt.setAttributes(coverAttrs);

      testData.setAttributes(attributes);
      testData.setRelationships(List.of(coverArt));
    }

    @Test
    @DisplayName("should extract id from data")
    void shouldExtractIdFromData() {
      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.id()).isEqualTo(mangaDexId);
    }

    @Test
    @DisplayName("should extract all titles from attributes")
    void shouldExtractAllTitlesFromAttributes() {
      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles())
          .hasSize(4)
          .contains("English Title", "Japanese Title", "Alt Title 1", "Alt Title 2");
    }

    @Test
    @DisplayName("should extract cover URL correctly")
    void shouldExtractCoverUrlCorrectly() {
      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl())
          .isEqualTo("https://uploads.mangadex.org/covers/" + mangaDexId + "/cover-image.jpg.256.jpg");
    }

    @Test
    @DisplayName("should build cover URL with correct format")
    void shouldBuildCoverUrlWithCorrectFormat() {
      testData.getRelationships().get(0).getAttributes().setFileName("abc123.png");

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl())
          .contains("uploads.mangadex.org/covers")
          .contains(mangaDexId)
          .contains("abc123.png.256.jpg");
    }
  }

  @Nested
  @DisplayName("fromMangaDexData() with minimal data")
  class FromMangaDexDataMinimalTests {

    @Test
    @DisplayName("should handle null attributes")
    void shouldHandleNullAttributes() {
      testData.setAttributes(null);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.id()).isEqualTo(mangaDexId);
      assertThat(output.coverUrl()).isNull();
      assertThat(output.titles()).isEmpty();
    }

    @Test
    @DisplayName("should handle null relationships")
    void shouldHandleNullRelationships() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));
      testData.setAttributes(attributes);
      testData.setRelationships(null);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.id()).isEqualTo(mangaDexId);
      assertThat(output.titles()).hasSize(1).contains("Title");
      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle empty relationships")
    void shouldHandleEmptyRelationships() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));
      testData.setAttributes(attributes);
      testData.setRelationships(List.of());

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle null title map")
    void shouldHandleNullTitleMap() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(null);
      attributes.setAltTitles(List.of(Map.of("en", "Alt 1")));
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles()).hasSize(1).contains("Alt 1");
    }

    @Test
    @DisplayName("should handle null alt titles list")
    void shouldHandleNullAltTitlesList() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));
      attributes.setAltTitles(null);
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles()).hasSize(1).contains("Title");
    }
  }

  @Nested
  @DisplayName("fromMangaDexData() cover art relationship")
  class FromMangaDexDataCoverArtTests {

    @Test
    @DisplayName("should find cover_art relationship")
    void shouldFindCoverArtRelationship() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));

      var artist = new MangaDexRelationship();
      artist.setType("artist");

      var coverArt = new MangaDexRelationship();
      coverArt.setType("cover_art");
      var coverAttrs = new MangaDexRelationshipAttributes();
      coverAttrs.setFileName("cover.jpg");
      coverArt.setAttributes(coverAttrs);

      var author = new MangaDexRelationship();
      author.setType("author");

      testData.setAttributes(attributes);
      testData.setRelationships(List.of(artist, coverArt, author));

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl()).isNotNull();
      assertThat(output.coverUrl()).contains("cover.jpg");
    }

    @Test
    @DisplayName("should ignore non-cover_art relationships")
    void shouldIgnoreNonCoverArtRelationships() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));

      var artist = new MangaDexRelationship();
      artist.setType("artist");
      var artistAttrs = new MangaDexRelationshipAttributes();
      artistAttrs.setFileName("artist.jpg");
      artist.setAttributes(artistAttrs);

      var author = new MangaDexRelationship();
      author.setType("author");

      testData.setAttributes(attributes);
      testData.setRelationships(List.of(artist, author));

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle cover art with null attributes")
    void shouldHandleCoverArtWithNullAttributes() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));

      var coverArt = new MangaDexRelationship();
      coverArt.setType("cover_art");
      coverArt.setAttributes(null);

      testData.setAttributes(attributes);
      testData.setRelationships(List.of(coverArt));

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle cover art with null fileName")
    void shouldHandleCoverArtWithNullFileName() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));

      var coverArt = new MangaDexRelationship();
      coverArt.setType("cover_art");
      var coverAttrs = new MangaDexRelationshipAttributes();
      coverAttrs.setFileName(null);
      coverArt.setAttributes(coverAttrs);

      testData.setAttributes(attributes);
      testData.setRelationships(List.of(coverArt));

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.coverUrl()).isNull();
    }
  }

  @Nested
  @DisplayName("fromMangaDexData() title handling")
  class FromMangaDexDataTitleHandlingTests {

    @Test
    @DisplayName("should collect all title values from main title map")
    void shouldCollectAllTitleValuesFromMainTitleMap() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of(
          "en", "English Title",
          "ja", "日本語タイトル",
          "pt", "Título em Português"
      ));
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles())
          .hasSize(3)
          .contains("English Title", "日本語タイトル", "Título em Português");
    }

    @Test
    @DisplayName("should collect all title values from alt titles list")
    void shouldCollectAllTitleValuesFromAltTitlesList() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Main Title"));
      attributes.setAltTitles(List.of(
          Map.of("en", "Alt 1", "ja", "Alt 1 JP"),
          Map.of("pt", "Alt 2")
      ));
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles())
          .hasSize(4)
          .contains("Main Title", "Alt 1", "Alt 1 JP", "Alt 2");
    }

    @Test
    @DisplayName("should handle empty alt titles")
    void shouldHandleEmptyAltTitles() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Title"));
      attributes.setAltTitles(List.of());
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles()).hasSize(1).contains("Title");
    }

    @Test
    @DisplayName("should handle empty title map in alt titles")
    void shouldHandleEmptyTitleMapInAltTitles() {
      var attributes = new MangaDexAttributes();
      attributes.setTitle(Map.of("en", "Main Title"));
      attributes.setAltTitles(List.of(
          Map.of("en", "Alt 1"),
          Map.of()
      ));
      testData.setAttributes(attributes);

      var output = MangaDexSearchOutput.fromMangaDexData(testData);

      assertThat(output.titles())
          .hasSize(2)
          .contains("Main Title", "Alt 1");
    }
  }

  @Nested
  @DisplayName("record structure")
  class RecordStructureTests {

    @Test
    @DisplayName("should create record with all fields")
    void shouldCreateRecordWithAllFields() {
      var output = new MangaDexSearchOutput(
          "id-123",
          "https://example.com/cover.jpg",
          List.of("Title 1", "Title 2")
      );

      assertThat(output.id()).isEqualTo("id-123");
      assertThat(output.coverUrl()).isEqualTo("https://example.com/cover.jpg");
      assertThat(output.titles()).hasSize(2).contains("Title 1", "Title 2");
    }

    @Test
    @DisplayName("should have equals method")
    void shouldHaveEqualsMethod() {
      var output1 = new MangaDexSearchOutput("id", "url", List.of("Title"));
      var output2 = new MangaDexSearchOutput("id", "url", List.of("Title"));

      assertThat(output1).isEqualTo(output2);
    }

    @Test
    @DisplayName("should have hashCode method")
    void shouldHaveHashCodeMethod() {
      var output1 = new MangaDexSearchOutput("id", "url", List.of("Title"));
      var output2 = new MangaDexSearchOutput("id", "url", List.of("Title"));

      assertThat(output1.hashCode()).isEqualTo(output2.hashCode());
    }

    @Test
    @DisplayName("should have toString method")
    void shouldHaveToStringMethod() {
      var output = new MangaDexSearchOutput("id", "url", List.of("Title"));

      assertThat(output.toString())
          .contains("id")
          .contains("coverUrl")
          .contains("titles");
    }
  }
}
