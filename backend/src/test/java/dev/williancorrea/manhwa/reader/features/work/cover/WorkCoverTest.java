package dev.williancorrea.manhwa.reader.features.work.cover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkCoverTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkCover_thenConstructSuccessfully() {
      var work = Work.builder().id(UUID.randomUUID()).build();

      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .work(work)
          .origin(SynchronizationOriginType.MANGADEX)
          .size(CoverType.LOW)
          .fileName("cover.jpg")
          .isOfficial(true)
          .build();

      assertNotNull(workCover.getId());
      assertEquals(work, workCover.getWork());
      assertEquals(SynchronizationOriginType.MANGADEX, workCover.getOrigin());
      assertEquals(CoverType.LOW, workCover.getSize());
      assertEquals("cover.jpg", workCover.getFileName());
      assertTrue(workCover.getIsOfficial());
    }

    @Test
    void givenNoArgs_whenCreateWorkCover_thenConstructSuccessfully() {
      var workCover = new WorkCover();

      assertNull(workCover.getId());
      assertNull(workCover.getWork());
      assertNull(workCover.getOrigin());
      assertNull(workCover.getSize());
      assertNull(workCover.getFileName());
      assertNull(workCover.getIsOfficial());
    }

    @Test
    void givenAllArgs_whenCreateWorkCover_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();

      var workCover = new WorkCover(id, work, SynchronizationOriginType.MEDIOCRESCAN, CoverType.HIGH, "high-cover.jpg", false);

      assertEquals(id, workCover.getId());
      assertEquals(work, workCover.getWork());
      assertEquals(SynchronizationOriginType.MEDIOCRESCAN, workCover.getOrigin());
      assertEquals(CoverType.HIGH, workCover.getSize());
      assertEquals("high-cover.jpg", workCover.getFileName());
      assertFalse(workCover.getIsOfficial());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkCover_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var workCover = WorkCover.builder().build();
      workCover.setWork(work);

      assertEquals(work, workCover.getWork());
    }

    @Test
    void givenWorkCover_whenSetOrigin_thenOriginIsUpdated() {
      var workCover = WorkCover.builder().build();
      workCover.setOrigin(SynchronizationOriginType.MANGADEX);

      assertEquals(SynchronizationOriginType.MANGADEX, workCover.getOrigin());
    }

    @Test
    void givenWorkCover_whenSetSize_thenSizeIsUpdated() {
      var workCover = WorkCover.builder().build();
      workCover.setSize(CoverType.MEDIUM);

      assertEquals(CoverType.MEDIUM, workCover.getSize());
    }

    @Test
    void givenWorkCover_whenSetFileName_thenFileNameIsUpdated() {
      var workCover = WorkCover.builder().build();
      workCover.setFileName("new-cover.jpg");

      assertEquals("new-cover.jpg", workCover.getFileName());
    }

    @Test
    void givenWorkCover_whenSetIsOfficial_thenIsOfficialIsUpdated() {
      var workCover = WorkCover.builder().build();
      workCover.setIsOfficial(true);

      assertTrue(workCover.getIsOfficial());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkCoversWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();

      var workCover1 = WorkCover.builder()
          .id(id)
          .work(work)
          .origin(SynchronizationOriginType.MANGADEX)
          .size(CoverType.LOW)
          .fileName("cover.jpg")
          .isOfficial(true)
          .build();

      var workCover2 = WorkCover.builder()
          .id(id)
          .work(work)
          .origin(SynchronizationOriginType.MANGADEX)
          .size(CoverType.LOW)
          .fileName("cover.jpg")
          .isOfficial(true)
          .build();

      assertEquals(workCover1, workCover2);
    }

    @Test
    void givenTwoWorkCoversWithDifferentIds_whenCompared_thenAreNotEqual() {
      var workCover1 = WorkCover.builder().id(UUID.randomUUID()).build();
      var workCover2 = WorkCover.builder().id(UUID.randomUUID()).build();

      assertFalse(workCover1.equals(workCover2));
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkCover_whenToString_thenContainsWorkCoverString() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .fileName("cover.jpg")
          .origin(SynchronizationOriginType.MANGADEX)
          .size(CoverType.LOW)
          .build();

      String toStringResult = workCover.toString();

      assertTrue(toStringResult.contains("WorkCover"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkCover_whenCreated_thenIsSerializable() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .build();

      assertTrue(workCover instanceof java.io.Serializable);
    }
  }

  @Nested
  class ValidationTests {

    @Test
    void givenWorkCoverWithNullOrigin_whenCreated_thenHasNullOrigin() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .origin(null)
          .build();

      assertNull(workCover.getOrigin());
    }

    @Test
    void givenWorkCoverWithNullSize_whenCreated_thenHasNullSize() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .size(null)
          .build();

      assertNull(workCover.getSize());
    }

    @Test
    void givenWorkCoverWithEmptyFileName_whenCreated_thenHasEmptyFileName() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .fileName("")
          .build();

      assertEquals("", workCover.getFileName());
    }

    @Test
    void givenWorkCoverWithNullFileName_whenCreated_thenHasNullFileName() {
      var workCover = WorkCover.builder()
          .id(UUID.randomUUID())
          .fileName(null)
          .build();

      assertNull(workCover.getFileName());
    }
  }
}
