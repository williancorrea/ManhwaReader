package dev.williancorrea.manhwa.reader.features.tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tag")
class TagTest {

  @Nested
  @DisplayName("Entity construction")
  class EntityConstructionTests {

    @Test
    @DisplayName("should create tag with builder")
    void shouldCreateTagWithBuilder() {
      var id = UUID.randomUUID();
      var group = TagGroupType.GENRE;

      var tag = Tag.builder()
          .id(id)
          .group(group)
          .name("Action")
          .alias1("Act")
          .alias2("Actn")
          .alias3("AGN")
          .build();

      assertThat(tag.getId()).isEqualTo(id);
      assertThat(tag.getGroup()).isEqualTo(group);
      assertThat(tag.getName()).isEqualTo("Action");
      assertThat(tag.getAlias1()).isEqualTo("Act");
      assertThat(tag.getAlias2()).isEqualTo("Actn");
      assertThat(tag.getAlias3()).isEqualTo("AGN");
    }

    @Test
    @DisplayName("should create tag with minimal fields")
    void shouldCreateTagWithMinimalFields() {
      var tag = Tag.builder()
          .group(TagGroupType.THEME)
          .build();

      assertThat(tag.getId()).isNull();
      assertThat(tag.getGroup()).isEqualTo(TagGroupType.THEME);
      assertThat(tag.getName()).isNull();
      assertThat(tag.getAlias1()).isNull();
      assertThat(tag.getAlias2()).isNull();
      assertThat(tag.getAlias3()).isNull();
    }

    @Test
    @DisplayName("should create tag with no-arg constructor")
    void shouldCreateTagWithNoArgConstructor() {
      var tag = new Tag();

      assertThat(tag.getId()).isNull();
      assertThat(tag.getGroup()).isNull();
      assertThat(tag.getName()).isNull();
    }

    @Test
    @DisplayName("should create tag with all-arg constructor")
    void shouldCreateTagWithAllArgConstructor() {
      var id = UUID.randomUUID();

      var tag = new Tag(id, TagGroupType.FORMAT, "Manga", "Manga1", "Manga2", "Manga3");

      assertThat(tag.getId()).isEqualTo(id);
      assertThat(tag.getGroup()).isEqualTo(TagGroupType.FORMAT);
      assertThat(tag.getName()).isEqualTo("Manga");
      assertThat(tag.getAlias1()).isEqualTo("Manga1");
      assertThat(tag.getAlias2()).isEqualTo("Manga2");
      assertThat(tag.getAlias3()).isEqualTo("Manga3");
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get id")
    void shouldSetAndGetId() {
      var tag = new Tag();
      var id = UUID.randomUUID();

      tag.setId(id);

      assertThat(tag.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should set and get group")
    void shouldSetAndGetGroup() {
      var tag = new Tag();

      tag.setGroup(TagGroupType.CONTENT);

      assertThat(tag.getGroup()).isEqualTo(TagGroupType.CONTENT);
    }

    @Test
    @DisplayName("should set and get name")
    void shouldSetAndGetName() {
      var tag = new Tag();

      tag.setName("Romance");

      assertThat(tag.getName()).isEqualTo("Romance");
    }

    @Test
    @DisplayName("should set and get aliases")
    void shouldSetAndGetAliases() {
      var tag = new Tag();

      tag.setAlias1("ROM");
      tag.setAlias2("Romantic");
      tag.setAlias3("Love");

      assertThat(tag.getAlias1()).isEqualTo("ROM");
      assertThat(tag.getAlias2()).isEqualTo("Romantic");
      assertThat(tag.getAlias3()).isEqualTo("Love");
    }
  }
}
