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

class WorkSynopsisTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkSynopsis_thenConstructSuccessfully() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var language = Language.builder().id(UUID.randomUUID()).build();

      var synopsis = WorkSynopsis.builder()
          .id(UUID.randomUUID())
          .work(work)
          .language(language)
          .description("Test Description")
          .build();

      assertNotNull(synopsis.getId());
      assertEquals(work, synopsis.getWork());
      assertEquals(language, synopsis.getLanguage());
      assertEquals("Test Description", synopsis.getDescription());
    }

    @Test
    void givenNoArgs_whenCreateWorkSynopsis_thenConstructSuccessfully() {
      var synopsis = new WorkSynopsis();

      assertNull(synopsis.getId());
      assertNull(synopsis.getWork());
      assertNull(synopsis.getLanguage());
      assertNull(synopsis.getDescription());
    }

    @Test
    void givenAllArgs_whenCreateWorkSynopsis_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var language = Language.builder().id(UUID.randomUUID()).build();

      var synopsis = new WorkSynopsis(id, work, language, "Test Description");

      assertEquals(id, synopsis.getId());
      assertEquals(work, synopsis.getWork());
      assertEquals(language, synopsis.getLanguage());
      assertEquals("Test Description", synopsis.getDescription());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkSynopsis_whenSetDescription_thenDescriptionIsUpdated() {
      var synopsis = WorkSynopsis.builder().build();
      synopsis.setDescription("New Description");

      assertEquals("New Description", synopsis.getDescription());
    }

    @Test
    void givenWorkSynopsis_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var synopsis = WorkSynopsis.builder().build();
      synopsis.setWork(work);

      assertEquals(work, synopsis.getWork());
    }

    @Test
    void givenWorkSynopsis_whenSetLanguage_thenLanguageIsUpdated() {
      var language = Language.builder().id(UUID.randomUUID()).build();
      var synopsis = WorkSynopsis.builder().build();
      synopsis.setLanguage(language);

      assertEquals(language, synopsis.getLanguage());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkSynopsisWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var language = Language.builder().id(UUID.randomUUID()).build();

      var synopsis1 = WorkSynopsis.builder()
          .id(id)
          .work(work)
          .language(language)
          .description("Test Description")
          .build();

      var synopsis2 = WorkSynopsis.builder()
          .id(id)
          .work(work)
          .language(language)
          .description("Test Description")
          .build();

      assertEquals(synopsis1, synopsis2);
    }

    @Test
    void givenTwoWorkSynopsisWithDifferentIds_whenCompared_thenAreNotEqual() {
      var synopsis1 = WorkSynopsis.builder().id(UUID.randomUUID()).build();
      var synopsis2 = WorkSynopsis.builder().id(UUID.randomUUID()).build();

      assertFalse(synopsis1.equals(synopsis2));
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkSynopsis_whenToString_thenContainsDescriptionValue() {
      var synopsis = WorkSynopsis.builder()
          .id(UUID.randomUUID())
          .description("Test Description")
          .build();

      String toStringResult = synopsis.toString();

      assertTrue(toStringResult.contains("WorkSynopsis"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkSynopsis_whenCreated_thenIsSerializable() {
      var synopsis = WorkSynopsis.builder()
          .id(UUID.randomUUID())
          .description("Test Description")
          .build();

      assertTrue(synopsis instanceof java.io.Serializable);
    }
  }
}
