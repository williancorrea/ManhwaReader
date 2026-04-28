package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkAuthorTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkAuthor_thenConstructSuccessfully() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var author = Author.builder().id(UUID.randomUUID()).type(AuthorType.WRITER).name("Test Author").build();

      var workAuthor = WorkAuthor.builder()
          .id(UUID.randomUUID())
          .work(work)
          .author(author)
          .build();

      assertNotNull(workAuthor.getId());
      assertEquals(work, workAuthor.getWork());
      assertEquals(author, workAuthor.getAuthor());
    }

    @Test
    void givenNoArgs_whenCreateWorkAuthor_thenConstructSuccessfully() {
      var workAuthor = new WorkAuthor();

      assertNull(workAuthor.getId());
      assertNull(workAuthor.getWork());
      assertNull(workAuthor.getAuthor());
    }

    @Test
    void givenAllArgs_whenCreateWorkAuthor_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var author = Author.builder().id(UUID.randomUUID()).type(AuthorType.WRITER).name("Test Author").build();

      var workAuthor = new WorkAuthor(id, work, author);

      assertEquals(id, workAuthor.getId());
      assertEquals(work, workAuthor.getWork());
      assertEquals(author, workAuthor.getAuthor());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkAuthor_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var workAuthor = WorkAuthor.builder().build();
      workAuthor.setWork(work);

      assertEquals(work, workAuthor.getWork());
    }

    @Test
    void givenWorkAuthor_whenSetAuthor_thenAuthorIsUpdated() {
      var author = Author.builder().id(UUID.randomUUID()).type(AuthorType.ARTIST).name("Test Author").build();
      var workAuthor = WorkAuthor.builder().build();
      workAuthor.setAuthor(author);

      assertEquals(author, workAuthor.getAuthor());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkAuthorsWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var author = Author.builder().id(UUID.randomUUID()).type(AuthorType.WRITER).name("Test Author").build();

      var workAuthor1 = WorkAuthor.builder()
          .id(id)
          .work(work)
          .author(author)
          .build();

      var workAuthor2 = WorkAuthor.builder()
          .id(id)
          .work(work)
          .author(author)
          .build();

      assertEquals(workAuthor1, workAuthor2);
    }

    @Test
    void givenTwoWorkAuthorsWithDifferentIds_whenCompared_thenAreNotEqual() {
      var workAuthor1 = WorkAuthor.builder().id(UUID.randomUUID()).build();
      var workAuthor2 = WorkAuthor.builder().id(UUID.randomUUID()).build();

      assertNotEquals(workAuthor1, workAuthor2);
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkAuthor_whenToString_thenContainsWorkAuthorString() {
      var workAuthor = WorkAuthor.builder()
          .id(UUID.randomUUID())
          .work(Work.builder().id(UUID.randomUUID()).build())
          .author(Author.builder().id(UUID.randomUUID()).build())
          .build();

      String toStringResult = workAuthor.toString();

      assertTrue(toStringResult.contains("WorkAuthor"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkAuthor_whenCreated_thenIsSerializable() {
      var workAuthor = WorkAuthor.builder()
          .id(UUID.randomUUID())
          .build();

      assertInstanceOf(Serializable.class, workAuthor);
    }
  }
}
