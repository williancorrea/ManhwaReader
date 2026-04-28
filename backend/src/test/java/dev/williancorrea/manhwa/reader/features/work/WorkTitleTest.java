package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkTitleTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkTitle_thenConstructSuccessfully() {
      var workId = UUID.randomUUID();
      var languageId = UUID.randomUUID();
      var work = Work.builder().id(workId).build();
      var language = Language.builder().id(languageId).build();

      var title = WorkTitle.builder()
          .id(UUID.randomUUID())
          .work(work)
          .language(language)
          .title("Test Title")
          .isOfficial(true)
          .build();

      assertNotNull(title.getId());
      assertEquals(work, title.getWork());
      assertEquals(language, title.getLanguage());
      assertEquals("Test Title", title.getTitle());
      assertTrue(title.getIsOfficial());
    }

    @Test
    void givenNoArgs_whenCreateWorkTitle_thenConstructSuccessfully() {
      var title = new WorkTitle();

      assertNull(title.getId());
      assertNull(title.getWork());
      assertNull(title.getLanguage());
      assertNull(title.getTitle());
      assertNull(title.getIsOfficial());
    }

    @Test
    void givenAllArgs_whenCreateWorkTitle_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var language = Language.builder().id(UUID.randomUUID()).build();

      var title = new WorkTitle(id, work, language, "Test Title", true);

      assertEquals(id, title.getId());
      assertEquals(work, title.getWork());
      assertEquals(language, title.getLanguage());
      assertEquals("Test Title", title.getTitle());
      assertTrue(title.getIsOfficial());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkTitle_whenSetTitle_thenTitleIsUpdated() {
      var title = WorkTitle.builder().build();
      title.setTitle("New Title");

      assertEquals("New Title", title.getTitle());
    }

    @Test
    void givenWorkTitle_whenSetIsOfficial_thenIsOfficialIsUpdated() {
      var title = WorkTitle.builder().build();
      title.setIsOfficial(true);

      assertTrue(title.getIsOfficial());
    }

    @Test
    void givenWorkTitle_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var title = WorkTitle.builder().build();
      title.setWork(work);

      assertEquals(work, title.getWork());
    }

    @Test
    void givenWorkTitle_whenSetLanguage_thenLanguageIsUpdated() {
      var language = Language.builder().id(UUID.randomUUID()).build();
      var title = WorkTitle.builder().build();
      title.setLanguage(language);

      assertEquals(language, title.getLanguage());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkTitlesWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var language = Language.builder().id(UUID.randomUUID()).build();

      var title1 = WorkTitle.builder()
          .id(id)
          .work(work)
          .language(language)
          .title("Test Title")
          .isOfficial(true)
          .build();

      var title2 = WorkTitle.builder()
          .id(id)
          .work(work)
          .language(language)
          .title("Test Title")
          .isOfficial(true)
          .build();

      assertEquals(title1, title2);
    }

    @Test
    void givenTwoWorkTitlesWithDifferentIds_whenCompared_thenAreNotEqual() {
      var title1 = WorkTitle.builder().id(UUID.randomUUID()).build();
      var title2 = WorkTitle.builder().id(UUID.randomUUID()).build();

      assertFalse(title1.equals(title2));
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkTitle_whenToString_thenContainsTitleValue() {
      var title = WorkTitle.builder()
          .id(UUID.randomUUID())
          .title("Test Title")
          .isOfficial(true)
          .build();

      String toStringResult = title.toString();

      assertTrue(toStringResult.contains("WorkTitle"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkTitle_whenCreated_thenIsSerializable() {
      var title = WorkTitle.builder()
          .id(UUID.randomUUID())
          .title("Test Title")
          .isOfficial(true)
          .build();

      assertTrue(title instanceof java.io.Serializable);
    }
  }
}
