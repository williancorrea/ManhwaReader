package dev.williancorrea.manhwa.reader.features.tag;

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
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagService")
class TagServiceTest {

  @Mock
  private TagRepository repository;

  @InjectMocks
  private TagService service;

  private UUID tagId;
  private Tag tag;

  @BeforeEach
  void setUp() {
    tagId = UUID.randomUUID();
    tag = Tag.builder()
        .id(tagId)
        .group(TagGroupType.GENRE)
        .name("Action")
        .build();
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save tag")
    void shouldSaveTag() {
      when(repository.saveAndFlush(tag)).thenReturn(tag);

      var result = service.save(tag);

      assertThat(result)
          .isNotNull()
          .isEqualTo(tag);
      verify(repository).saveAndFlush(tag);
    }

    @Test
    @DisplayName("should save new tag and return with id")
    void shouldSaveNewTagAndReturnWithId() {
      var newTag = Tag.builder()
          .group(TagGroupType.GENRE)
          .name("Romance")
          .build();
      var savedTag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name("Romance")
          .build();

      when(repository.saveAndFlush(newTag)).thenReturn(savedTag);

      var result = service.save(newTag);

      assertThat(result.getId()).isNotNull();
      verify(repository).saveAndFlush(newTag);
    }

    @Test
    @DisplayName("should update existing tag")
    void shouldUpdateExistingTag() {
      var updatedTag = Tag.builder()
          .id(tagId)
          .group(TagGroupType.GENRE)
          .name("Comedy")
          .build();

      when(repository.saveAndFlush(updatedTag)).thenReturn(updatedTag);

      var result = service.save(updatedTag);

      assertThat(result.getName()).isEqualTo("Comedy");
      verify(repository).saveAndFlush(updatedTag);
    }

    @Test
    @DisplayName("should save tag with all aliases")
    void shouldSaveTagWithAllAliases() {
      var tagWithAliases = Tag.builder()
          .group(TagGroupType.GENRE)
          .name("Science Fiction")
          .alias1("SciFi")
          .alias2("SF")
          .alias3("Sci-Fi")
          .build();

      when(repository.saveAndFlush(tagWithAliases)).thenReturn(tagWithAliases);

      var result = service.save(tagWithAliases);

      assertThat(result.getAlias1()).isEqualTo("SciFi");
      assertThat(result.getAlias2()).isEqualTo("SF");
      assertThat(result.getAlias3()).isEqualTo("Sci-Fi");
      verify(repository).saveAndFlush(tagWithAliases);
    }
  }

  @Nested
  @DisplayName("findOrCreate()")
  class FindOrCreateTests {

    @Test
    @DisplayName("should return existing tag")
    void shouldReturnExistingTag() {
      when(repository.findByGroupAndName("GENRE", "Action"))
          .thenReturn(Optional.of(tag));

      var result = service.findOrCreate(TagGroupType.GENRE, "Action");

      assertThat(result)
          .isNotNull()
          .isEqualTo(tag);
      verify(repository).findByGroupAndName("GENRE", "Action");
      verify(repository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("should create new tag when not found")
    void shouldCreateNewTagWhenNotFound() {
      var newTag = Tag.builder()
          .group(TagGroupType.GENRE)
          .alias1("Comedy")
          .build();

      when(repository.findByGroupAndName("GENRE", "Comedy"))
          .thenReturn(Optional.empty());
      when(repository.saveAndFlush(any(Tag.class)))
          .thenReturn(newTag);

      var result = service.findOrCreate(TagGroupType.GENRE, "Comedy");

      assertThat(result).isNotNull();
      assertThat(result.getGroup()).isEqualTo(TagGroupType.GENRE);
      assertThat(result.getAlias1()).isEqualTo("Comedy");
      verify(repository).findByGroupAndName("GENRE", "Comedy");
      verify(repository).saveAndFlush(any(Tag.class));
    }

    @Test
    @DisplayName("should set name as alias1 when creating tag")
    void shouldSetNameAsAlias1WhenCreatingTag() {
      when(repository.findByGroupAndName("GENRE", "Mystery"))
          .thenReturn(Optional.empty());
      when(repository.saveAndFlush(any(Tag.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = service.findOrCreate(TagGroupType.GENRE, "Mystery");

      assertThat(result.getAlias1()).isEqualTo("Mystery");
      verify(repository).saveAndFlush(any(Tag.class));
    }

    @Test
    @DisplayName("should accept name with leading/trailing whitespace")
    void shouldAcceptNameWithLeadingTrailingWhitespace() {
      when(repository.findByGroupAndName("GENRE", "  Thriller  "))
          .thenReturn(Optional.of(tag));

      var result = service.findOrCreate(TagGroupType.GENRE, "  Thriller  ");

      assertThat(result).isNotNull();
      verify(repository).findByGroupAndName("GENRE", "  Thriller  ");
    }

    @Test
    @DisplayName("should throw NPE when group is null")
    void shouldThrowNPEWhenGroupIsNull() {
      assertThatNullPointerException()
          .isThrownBy(() -> service.findOrCreate(null, "Action"));
      verify(repository, never()).findByGroupAndName(any(), any());
    }

    @Test
    @DisplayName("should throw NPE when name is null")
    void shouldThrowNPEWhenNameIsNull() {
      assertThatNullPointerException()
          .isThrownBy(() -> service.findOrCreate(TagGroupType.GENRE, null));
      verify(repository, never()).findByGroupAndName(any(), any());
    }

    @Test
    @DisplayName("should throw NPE when name is blank")
    void shouldThrowNPEWhenNameIsBlank() {
      assertThatNullPointerException()
          .isThrownBy(() -> service.findOrCreate(TagGroupType.GENRE, "   "));
      verify(repository, never()).findByGroupAndName(any(), any());
    }

    @Test
    @DisplayName("should throw NPE when name is empty string")
    void shouldThrowNPEWhenNameIsEmptyString() {
      assertThatNullPointerException()
          .isThrownBy(() -> service.findOrCreate(TagGroupType.GENRE, ""));
      verify(repository, never()).findByGroupAndName(any(), any());
    }

    @Test
    @DisplayName("should handle different tag groups")
    void shouldHandleDifferentTagGroups() {
      var themeTag = Tag.builder()
          .group(TagGroupType.THEME)
          .name("Dark")
          .build();

      when(repository.findByGroupAndName("THEME", "Dark"))
          .thenReturn(Optional.of(themeTag));

      var result = service.findOrCreate(TagGroupType.THEME, "Dark");

      assertThat(result.getGroup()).isEqualTo(TagGroupType.THEME);
      verify(repository).findByGroupAndName("THEME", "Dark");
    }

    @Test
    @DisplayName("should create tag for FORMAT group")
    void shouldCreateTagForFormatGroup() {
      when(repository.findByGroupAndName("FORMAT", "Manga"))
          .thenReturn(Optional.empty());
      when(repository.saveAndFlush(any(Tag.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = service.findOrCreate(TagGroupType.FORMAT, "Manga");

      assertThat(result.getGroup()).isEqualTo(TagGroupType.FORMAT);
      verify(repository).saveAndFlush(any(Tag.class));
    }

    @Test
    @DisplayName("should create tag for CONTENT group")
    void shouldCreateTagForContentGroup() {
      when(repository.findByGroupAndName("CONTENT", "Violence"))
          .thenReturn(Optional.empty());
      when(repository.saveAndFlush(any(Tag.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = service.findOrCreate(TagGroupType.CONTENT, "Violence");

      assertThat(result.getGroup()).isEqualTo(TagGroupType.CONTENT);
      verify(repository).saveAndFlush(any(Tag.class));
    }

    @Test
    @DisplayName("should preserve case when creating new tag")
    void shouldPreserveCaseWhenCreatingNewTag() {
      var newTag = Tag.builder()
          .group(TagGroupType.GENRE)
          .alias1("FantAsY")
          .build();

      when(repository.findByGroupAndName("GENRE", "FantAsY"))
          .thenReturn(Optional.empty());
      when(repository.saveAndFlush(any(Tag.class)))
          .thenReturn(newTag);

      var result = service.findOrCreate(TagGroupType.GENRE, "FantAsY");

      assertThat(result.getAlias1()).isEqualTo("FantAsY");
    }
  }
}
