package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.tag.Tag;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkTagTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkTag_thenConstructSuccessfully() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var tag = Tag.builder().id(UUID.randomUUID()).group(TagGroupType.GENRE).name("Action").build();

      var workTag = WorkTag.builder()
          .id(UUID.randomUUID())
          .work(work)
          .tag(tag)
          .build();

      assertNotNull(workTag.getId());
      assertEquals(work, workTag.getWork());
      assertEquals(tag, workTag.getTag());
    }

    @Test
    void givenNoArgs_whenCreateWorkTag_thenConstructSuccessfully() {
      var workTag = new WorkTag();

      assertNull(workTag.getId());
      assertNull(workTag.getWork());
      assertNull(workTag.getTag());
    }

    @Test
    void givenAllArgs_whenCreateWorkTag_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var tag = Tag.builder().id(UUID.randomUUID()).group(TagGroupType.GENRE).name("Action").build();

      var workTag = new WorkTag(id, work, tag);

      assertEquals(id, workTag.getId());
      assertEquals(work, workTag.getWork());
      assertEquals(tag, workTag.getTag());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkTag_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var workTag = WorkTag.builder().build();
      workTag.setWork(work);

      assertEquals(work, workTag.getWork());
    }

    @Test
    void givenWorkTag_whenSetTag_thenTagIsUpdated() {
      var tag = Tag.builder().id(UUID.randomUUID()).group(TagGroupType.THEME).name("Adventure").build();
      var workTag = WorkTag.builder().build();
      workTag.setTag(tag);

      assertEquals(tag, workTag.getTag());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkTagsWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var tag = Tag.builder().id(UUID.randomUUID()).group(TagGroupType.GENRE).name("Action").build();

      var workTag1 = WorkTag.builder()
          .id(id)
          .work(work)
          .tag(tag)
          .build();

      var workTag2 = WorkTag.builder()
          .id(id)
          .work(work)
          .tag(tag)
          .build();

      assertEquals(workTag1, workTag2);
    }

    @Test
    void givenTwoWorkTagsWithDifferentIds_whenCompared_thenAreNotEqual() {
      var workTag1 = WorkTag.builder().id(UUID.randomUUID()).build();
      var workTag2 = WorkTag.builder().id(UUID.randomUUID()).build();

      assertFalse(workTag1.equals(workTag2));
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkTag_whenToString_thenContainsWorkTagString() {
      var workTag = WorkTag.builder()
          .id(UUID.randomUUID())
          .work(Work.builder().id(UUID.randomUUID()).build())
          .tag(Tag.builder().id(UUID.randomUUID()).build())
          .build();

      String toStringResult = workTag.toString();

      assertTrue(toStringResult.contains("WorkTag"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkTag_whenCreated_thenIsSerializable() {
      var workTag = WorkTag.builder()
          .id(UUID.randomUUID())
          .build();

      assertTrue(workTag instanceof java.io.Serializable);
    }
  }
}
