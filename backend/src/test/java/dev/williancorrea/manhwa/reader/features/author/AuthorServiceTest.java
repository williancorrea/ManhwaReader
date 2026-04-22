package dev.williancorrea.manhwa.reader.features.author;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService")
class AuthorServiceTest {

  @Mock
  private AuthorRepository repository;

  @InjectMocks
  private AuthorService authorService;

  private UUID authorId;
  private String authorName;
  private Author testAuthor;

  @BeforeEach
  void setUp() {
    authorId = UUID.randomUUID();
    authorName = "Test Author";
    testAuthor = Author.builder()
        .id(authorId)
        .name(authorName)
        .type(AuthorType.AUTHOR)
        .biography("Test biography")
        .twitter("@testauthor")
        .build();
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save author successfully")
    void shouldSaveAuthorSuccessfully() {
      when(repository.save(any(Author.class))).thenReturn(testAuthor);

      var result = authorService.save(testAuthor);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(authorId);
      assertThat(result.getName()).isEqualTo(authorName);
      assertThat(result.getType()).isEqualTo(AuthorType.AUTHOR);
      verify(repository).save(testAuthor);
    }

    @Test
    @DisplayName("should save author with all social media fields")
    void shouldSaveAuthorWithAllSocialMediaFields() {
      var authorWithSocial = Author.builder()
          .id(authorId)
          .name(authorName)
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

      when(repository.save(any(Author.class))).thenReturn(authorWithSocial);

      var result = authorService.save(authorWithSocial);

      assertThat(result).isNotNull();
      assertThat(result.getPixiv()).isEqualTo("pixiv123");
      assertThat(result.getMelonBook()).isEqualTo("melonbook123");
      assertThat(result.getFanBox()).isEqualTo("fanbox123");
      assertThat(result.getBooth()).isEqualTo("booth123");
      assertThat(result.getNamicomi()).isEqualTo("namicomi123");
      assertThat(result.getNicoVideo()).isEqualTo("nicovideo123");
      assertThat(result.getSkeb()).isEqualTo("skeb123");
      assertThat(result.getFantia()).isEqualTo("fantia123");
      assertThat(result.getTumblr()).isEqualTo("tumblr123");
      assertThat(result.getYoutube()).isEqualTo("youtube123");
      assertThat(result.getWeibo()).isEqualTo("weibo123");
      assertThat(result.getNaver()).isEqualTo("naver123");
      assertThat(result.getWebsite()).isEqualTo("https://example.com");
      verify(repository).save(authorWithSocial);
    }

    @Test
    @DisplayName("should save author with different types")
    void shouldSaveAuthorWithDifferentTypes() {
      var writerAuthor = Author.builder()
          .id(UUID.randomUUID())
          .name("Test Writer")
          .type(AuthorType.WRITER)
          .build();

      when(repository.save(any(Author.class))).thenReturn(writerAuthor);

      var result = authorService.save(writerAuthor);

      assertThat(result.getType()).isEqualTo(AuthorType.WRITER);
      verify(repository).save(writerAuthor);
    }

    @Test
    @DisplayName("should save author with null optional fields")
    void shouldSaveAuthorWithNullOptionalFields() {
      var minimalAuthor = Author.builder()
          .id(authorId)
          .name(authorName)
          .type(AuthorType.AUTHOR)
          .build();

      when(repository.save(any(Author.class))).thenReturn(minimalAuthor);

      var result = authorService.save(minimalAuthor);

      assertThat(result).isNotNull();
      assertThat(result.getBiography()).isNull();
      assertThat(result.getTwitter()).isNull();
      assertThat(result.getPixiv()).isNull();
      verify(repository).save(minimalAuthor);
    }
  }

  @Nested
  @DisplayName("findByName()")
  class FindByNameTests {

    @Test
    @DisplayName("should find author by name successfully")
    void shouldFindAuthorByNameSuccessfully() {
      when(repository.findByName(authorName)).thenReturn(Optional.of(testAuthor));

      var result = authorService.findByName(authorName);

      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(authorId);
      assertThat(result.get().getName()).isEqualTo(authorName);
      verify(repository).findByName(authorName);
    }

    @Test
    @DisplayName("should return empty optional when author not found")
    void shouldReturnEmptyOptionalWhenAuthorNotFound() {
      var nonExistentName = "Non Existent Author";
      when(repository.findByName(nonExistentName)).thenReturn(Optional.empty());

      var result = authorService.findByName(nonExistentName);

      assertThat(result).isEmpty();
      verify(repository).findByName(nonExistentName);
    }

    @Test
    @DisplayName("should find author by exact name match")
    void shouldFindAuthorByExactNameMatch() {
      var exactName = "Exact Author Name";
      var exactAuthor = Author.builder()
          .id(UUID.randomUUID())
          .name(exactName)
          .type(AuthorType.ARTIST)
          .build();

      when(repository.findByName(exactName)).thenReturn(Optional.of(exactAuthor));

      var result = authorService.findByName(exactName);

      assertThat(result).isPresent();
      assertThat(result.get().getName()).isEqualTo(exactName);
      verify(repository).findByName(exactName);
    }
  }

  @Nested
  @DisplayName("findOrCreate()")
  class FindOrCreateTests {

    @Test
    @DisplayName("should return existing author when found by name")
    void shouldReturnExistingAuthorWhenFound() {
      when(repository.findByName(authorName)).thenReturn(Optional.of(testAuthor));

      var result = authorService.findOrCreate(testAuthor);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(authorId);
      assertThat(result.getName()).isEqualTo(authorName);
      verify(repository).findByName(authorName);
    }

    @Test
    @DisplayName("should create new author when not found")
    void shouldCreateNewAuthorWhenNotFound() {
      var newAuthor = Author.builder()
          .name("New Author")
          .type(AuthorType.WRITER)
          .build();

      var savedAuthor = Author.builder()
          .id(UUID.randomUUID())
          .name("New Author")
          .type(AuthorType.WRITER)
          .build();

      when(repository.findByName("New Author")).thenReturn(Optional.empty());
      when(repository.save(any(Author.class))).thenReturn(savedAuthor);

      var result = authorService.findOrCreate(newAuthor);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isNotNull();
      assertThat(result.getName()).isEqualTo("New Author");
      verify(repository).findByName("New Author");
      verify(repository).save(any(Author.class));
    }

    @Test
    @DisplayName("should prefer existing author over creating new one")
    void shouldPreferExistingAuthor() {
      var authorToSearch = Author.builder()
          .name(authorName)
          .type(AuthorType.ARTIST)
          .biography("New biography")
          .build();

      when(repository.findByName(authorName)).thenReturn(Optional.of(testAuthor));

      var result = authorService.findOrCreate(authorToSearch);

      assertThat(result.getId()).isEqualTo(testAuthor.getId());
      assertThat(result.getName()).isEqualTo(testAuthor.getName());
      verify(repository).findByName(authorName);
    }

    @Test
    @DisplayName("should save author with complete information")
    void shouldSaveAuthorWithCompleteInformation() {
      var newAuthorWithInfo = Author.builder()
          .name("Complete Author")
          .type(AuthorType.AUTHOR)
          .biography("Full biography")
          .twitter("@author")
          .pixiv("pixiv456")
          .website("https://author.com")
          .build();

      var savedAuthorWithInfo = Author.builder()
          .id(UUID.randomUUID())
          .name("Complete Author")
          .type(AuthorType.AUTHOR)
          .biography("Full biography")
          .twitter("@author")
          .pixiv("pixiv456")
          .website("https://author.com")
          .build();

      when(repository.findByName("Complete Author")).thenReturn(Optional.empty());
      when(repository.save(any(Author.class))).thenReturn(savedAuthorWithInfo);

      var result = authorService.findOrCreate(newAuthorWithInfo);

      assertThat(result.getId()).isNotNull();
      assertThat(result.getBiography()).isEqualTo("Full biography");
      assertThat(result.getTwitter()).isEqualTo("@author");
      assertThat(result.getPixiv()).isEqualTo("pixiv456");
      verify(repository).save(any(Author.class));
    }
  }
}
