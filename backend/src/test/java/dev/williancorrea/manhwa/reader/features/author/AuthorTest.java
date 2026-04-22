package dev.williancorrea.manhwa.reader.features.author;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Author")
class AuthorTest {

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create author with builder")
    void shouldCreateAuthorWithBuilder() {
      var id = UUID.randomUUID();
      var author = Author.builder()
          .id(id)
          .name("Test Author")
          .type(AuthorType.AUTHOR)
          .biography("Test biography")
          .twitter("@testauthor")
          .build();

      assertThat(author).isNotNull();
      assertThat(author.getId()).isEqualTo(id);
      assertThat(author.getName()).isEqualTo("Test Author");
      assertThat(author.getType()).isEqualTo(AuthorType.AUTHOR);
      assertThat(author.getBiography()).isEqualTo("Test biography");
      assertThat(author.getTwitter()).isEqualTo("@testauthor");
    }

    @Test
    @DisplayName("should create author with all social media fields")
    void shouldCreateAuthorWithAllSocialMediaFields() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .name("Social Media Author")
          .type(AuthorType.ARTIST)
          .biography("Artist biography")
          .twitter("@artist")
          .pixiv("pixiv123")
          .melonBook("melonbook123")
          .fanBox("fanbox123")
          .booth("booth123")
          .namicomi("namicomi123")
          .nicoVideo("nicovideo123")
          .skeb("skeb123")
          .fantia("fantia123")
          .tumblr("tumblr123")
          .youtube("youtube123")
          .weibo("weibo123")
          .naver("naver123")
          .website("https://example.com")
          .build();

      assertThat(author.getPixiv()).isEqualTo("pixiv123");
      assertThat(author.getMelonBook()).isEqualTo("melonbook123");
      assertThat(author.getFanBox()).isEqualTo("fanbox123");
      assertThat(author.getBooth()).isEqualTo("booth123");
      assertThat(author.getNamicomi()).isEqualTo("namicomi123");
      assertThat(author.getNicoVideo()).isEqualTo("nicovideo123");
      assertThat(author.getSkeb()).isEqualTo("skeb123");
      assertThat(author.getFantia()).isEqualTo("fantia123");
      assertThat(author.getTumblr()).isEqualTo("tumblr123");
      assertThat(author.getYoutube()).isEqualTo("youtube123");
      assertThat(author.getWeibo()).isEqualTo("weibo123");
      assertThat(author.getNaver()).isEqualTo("naver123");
      assertThat(author.getWebsite()).isEqualTo("https://example.com");
    }

    @Test
    @DisplayName("should create author with minimum required fields")
    void shouldCreateAuthorWithMinimumFields() {
      var id = UUID.randomUUID();
      var author = Author.builder()
          .id(id)
          .name("Minimal Author")
          .type(AuthorType.WRITER)
          .build();

      assertThat(author).isNotNull();
      assertThat(author.getId()).isEqualTo(id);
      assertThat(author.getName()).isEqualTo("Minimal Author");
      assertThat(author.getType()).isEqualTo(AuthorType.WRITER);
      assertThat(author.getBiography()).isNull();
      assertThat(author.getTwitter()).isNull();
      assertThat(author.getPixiv()).isNull();
    }
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create author with no-args constructor")
    void shouldCreateAuthorWithNoArgsConstructor() {
      var author = new Author();

      assertThat(author).isNotNull();
      assertThat(author.getId()).isNull();
      assertThat(author.getName()).isNull();
      assertThat(author.getType()).isNull();
    }

    @Test
    @DisplayName("should create author with all-args constructor")
    void shouldCreateAuthorWithAllArgsConstructor() {
      var id = UUID.randomUUID();
      var author = new Author(
          id,
          "Full Constructor Author",
          AuthorType.ARTIST,
          "Biography",
          "@twitter",
          "pixiv123",
          "melonbook",
          "fanbox",
          "booth",
          "namicomi",
          "nicovideo",
          "skeb",
          "fantia",
          "tumblr",
          "youtube",
          "weibo",
          "naver",
          "https://website.com"
      );

      assertThat(author.getId()).isEqualTo(id);
      assertThat(author.getName()).isEqualTo("Full Constructor Author");
      assertThat(author.getType()).isEqualTo(AuthorType.ARTIST);
      assertThat(author.getBiography()).isEqualTo("Biography");
      assertThat(author.getTwitter()).isEqualTo("@twitter");
      assertThat(author.getPixiv()).isEqualTo("pixiv123");
      assertThat(author.getMelonBook()).isEqualTo("melonbook");
      assertThat(author.getFanBox()).isEqualTo("fanbox");
      assertThat(author.getBooth()).isEqualTo("booth");
      assertThat(author.getNamicomi()).isEqualTo("namicomi");
      assertThat(author.getNicoVideo()).isEqualTo("nicovideo");
      assertThat(author.getSkeb()).isEqualTo("skeb");
      assertThat(author.getFantia()).isEqualTo("fantia");
      assertThat(author.getTumblr()).isEqualTo("tumblr");
      assertThat(author.getYoutube()).isEqualTo("youtube");
      assertThat(author.getWeibo()).isEqualTo("weibo");
      assertThat(author.getNaver()).isEqualTo("naver");
      assertThat(author.getWebsite()).isEqualTo("https://website.com");
    }
  }

  @Nested
  @DisplayName("Setters")
  class SetterTests {

    @Test
    @DisplayName("should set id")
    void shouldSetId() {
      var author = new Author();
      var id = UUID.randomUUID();

      author.setId(id);

      assertThat(author.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should set name")
    void shouldSetName() {
      var author = new Author();

      author.setName("Updated Name");

      assertThat(author.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("should set type")
    void shouldSetType() {
      var author = new Author();

      author.setType(AuthorType.WRITER);

      assertThat(author.getType()).isEqualTo(AuthorType.WRITER);
    }

    @Test
    @DisplayName("should set biography")
    void shouldSetBiography() {
      var author = new Author();

      author.setBiography("Updated biography");

      assertThat(author.getBiography()).isEqualTo("Updated biography");
    }

    @Test
    @DisplayName("should set all social media fields")
    void shouldSetAllSocialMediaFields() {
      var author = new Author();

      author.setTwitter("@twitter");
      author.setPixiv("pixiv");
      author.setMelonBook("melonbook");
      author.setFanBox("fanbox");
      author.setBooth("booth");
      author.setNamicomi("namicomi");
      author.setNicoVideo("nicovideo");
      author.setSkeb("skeb");
      author.setFantia("fantia");
      author.setTumblr("tumblr");
      author.setYoutube("youtube");
      author.setWeibo("weibo");
      author.setNaver("naver");
      author.setWebsite("website");

      assertThat(author.getTwitter()).isEqualTo("@twitter");
      assertThat(author.getPixiv()).isEqualTo("pixiv");
      assertThat(author.getMelonBook()).isEqualTo("melonbook");
      assertThat(author.getFanBox()).isEqualTo("fanbox");
      assertThat(author.getBooth()).isEqualTo("booth");
      assertThat(author.getNamicomi()).isEqualTo("namicomi");
      assertThat(author.getNicoVideo()).isEqualTo("nicovideo");
      assertThat(author.getSkeb()).isEqualTo("skeb");
      assertThat(author.getFantia()).isEqualTo("fantia");
      assertThat(author.getTumblr()).isEqualTo("tumblr");
      assertThat(author.getYoutube()).isEqualTo("youtube");
      assertThat(author.getWeibo()).isEqualTo("weibo");
      assertThat(author.getNaver()).isEqualTo("naver");
      assertThat(author.getWebsite()).isEqualTo("website");
    }
  }

  @Nested
  @DisplayName("Equality")
  class EqualityTests {

    @Test
    @DisplayName("should have same id means equal authors")
    void shouldConsiderSameIdAsEqual() {
      var id = UUID.randomUUID();
      var author1 = Author.builder().id(id).name("Author 1").type(AuthorType.AUTHOR).build();
      var author2 = Author.builder().id(id).name("Author 1").type(AuthorType.AUTHOR).build();

      assertThat(author1).isEqualTo(author2);
    }

    @Test
    @DisplayName("should have different ids means different authors")
    void shouldConsiderDifferentIdsAsDifferent() {
      var author1 = Author.builder().id(UUID.randomUUID()).name("Author 1").type(AuthorType.AUTHOR).build();
      var author2 = Author.builder().id(UUID.randomUUID()).name("Author 1").type(AuthorType.AUTHOR).build();

      assertThat(author1).isNotEqualTo(author2);
    }
  }

  @Nested
  @DisplayName("AuthorType enum")
  class AuthorTypeTests {

    @Test
    @DisplayName("should have WRITER enum value")
    void shouldHaveWriterType() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .name("Writer")
          .type(AuthorType.WRITER)
          .build();

      assertThat(author.getType()).isEqualTo(AuthorType.WRITER);
    }

    @Test
    @DisplayName("should have ARTIST enum value")
    void shouldHaveArtistType() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .name("Artist")
          .type(AuthorType.ARTIST)
          .build();

      assertThat(author.getType()).isEqualTo(AuthorType.ARTIST);
    }

    @Test
    @DisplayName("should have AUTHOR enum value")
    void shouldHaveAuthorType() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .name("Author")
          .type(AuthorType.AUTHOR)
          .build();

      assertThat(author.getType()).isEqualTo(AuthorType.AUTHOR);
    }
  }
}
