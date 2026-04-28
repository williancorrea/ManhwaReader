package dev.williancorrea.manhwa.reader.features.work.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkLinkTest {

  @Nested
  class CreationTests {

    @Test
    void givenBuilder_whenCreateWorkLink_thenConstructSuccessfully() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var site = Site.builder().id(UUID.randomUUID()).build();

      var workLink = WorkLink.builder()
          .id(UUID.randomUUID())
          .work(work)
          .site(site)
          .code(SiteType.MANGADEX)
          .link("https://example.com/link")
          .build();

      assertNotNull(workLink.getId());
      assertEquals(work, workLink.getWork());
      assertEquals(site, workLink.getSite());
      assertEquals(SiteType.MANGADEX, workLink.getCode());
      assertEquals("https://example.com/link", workLink.getLink());
    }

    @Test
    void givenNoArgs_whenCreateWorkLink_thenConstructSuccessfully() {
      var workLink = new WorkLink();

      assertNull(workLink.getId());
      assertNull(workLink.getWork());
      assertNull(workLink.getSite());
      assertNull(workLink.getCode());
      assertNull(workLink.getLink());
    }

    @Test
    void givenAllArgs_whenCreateWorkLink_thenConstructSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var site = Site.builder().id(UUID.randomUUID()).build();

      var workLink = new WorkLink(id, work, site, SiteType.MANGADEX, "https://example.com");

      assertEquals(id, workLink.getId());
      assertEquals(work, workLink.getWork());
      assertEquals(site, workLink.getSite());
      assertEquals(SiteType.MANGADEX, workLink.getCode());
      assertEquals("https://example.com", workLink.getLink());
    }
  }

  @Nested
  class SettersTests {

    @Test
    void givenWorkLink_whenSetWork_thenWorkIsUpdated() {
      var work = Work.builder().id(UUID.randomUUID()).build();
      var workLink = WorkLink.builder().build();
      workLink.setWork(work);

      assertEquals(work, workLink.getWork());
    }

    @Test
    void givenWorkLink_whenSetSite_thenSiteIsUpdated() {
      var site = Site.builder().id(UUID.randomUUID()).build();
      var workLink = WorkLink.builder().build();
      workLink.setSite(site);

      assertEquals(site, workLink.getSite());
    }

    @Test
    void givenWorkLink_whenSetCode_thenCodeIsUpdated() {
      var workLink = WorkLink.builder().build();
      workLink.setCode(SiteType.MANGADEX);

      assertEquals(SiteType.MANGADEX, workLink.getCode());
    }

    @Test
    void givenWorkLink_whenSetLink_thenLinkIsUpdated() {
      var workLink = WorkLink.builder().build();
      workLink.setLink("https://new-link.com");

      assertEquals("https://new-link.com", workLink.getLink());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorkLinksWithSameData_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(UUID.randomUUID()).build();
      var site = Site.builder().id(UUID.randomUUID()).build();

      var workLink1 = WorkLink.builder()
          .id(id)
          .work(work)
          .site(site)
          .code(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      var workLink2 = WorkLink.builder()
          .id(id)
          .work(work)
          .site(site)
          .code(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      assertEquals(workLink1, workLink2);
    }

    @Test
    void givenTwoWorkLinksWithDifferentIds_whenCompared_thenAreNotEqual() {
      var workLink1 = WorkLink.builder().id(UUID.randomUUID()).build();
      var workLink2 = WorkLink.builder().id(UUID.randomUUID()).build();

      assertFalse(workLink1.equals(workLink2));
    }
  }

  @Nested
  class ToStringTests {

    @Test
    void givenWorkLink_whenToString_thenContainsWorkLinkString() {
      var workLink = WorkLink.builder()
          .id(UUID.randomUUID())
          .code(SiteType.MANGADEX)
          .link("https://example.com")
          .build();

      String toStringResult = workLink.toString();

      assertTrue(toStringResult.contains("WorkLink"));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWorkLink_whenCreated_thenIsSerializable() {
      var workLink = WorkLink.builder()
          .id(UUID.randomUUID())
          .build();

      assertTrue(workLink instanceof java.io.Serializable);
    }
  }
}
