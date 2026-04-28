package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import dev.williancorrea.manhwa.reader.features.tag.Tag;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.cover.WorkCover;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorkTest {

  @Nested
  class HasSynchronizationOriginTests {

    @Test
    void givenWorkWithSynchronizations_whenHasSynchronizationOrigin_thenReturnTrue() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .synchronizations(List.of(
              WorkSynchronization.builder()
                  .origin(SynchronizationOriginType.MANGADEX)
                  .build()
          ))
          .build();

      assertTrue(work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX));
    }

    @Test
    void givenWorkWithoutRequestedOrigin_whenHasSynchronizationOrigin_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .synchronizations(List.of(
              WorkSynchronization.builder()
                  .origin(SynchronizationOriginType.MEDIOCRESCAN)
                  .build()
          ))
          .build();

      assertFalse(work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX));
    }

    @Test
    void givenWorkWithEmptySynchronizations_whenHasSynchronizationOrigin_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .synchronizations(List.of())
          .build();

      assertFalse(work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX));
    }

    @Test
    void givenWorkWithNullSynchronizations_whenHasSynchronizationOrigin_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .synchronizations(null)
          .build();

      assertFalse(work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX));
    }

    @Test
    void givenWorkWithMultipleSynchronizations_whenHasSynchronizationOrigin_thenReturnTrueIfAnyMatch() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .synchronizations(List.of(
              WorkSynchronization.builder()
                  .origin(SynchronizationOriginType.MEDIOCRESCAN)
                  .build(),
              WorkSynchronization.builder()
                  .origin(SynchronizationOriginType.MANGADEX)
                  .build(),
              WorkSynchronization.builder()
                  .origin(SynchronizationOriginType.MANGOTOONS)
                  .build()
          ))
          .build();

      assertTrue(work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX));
    }
  }

  @Nested
  class HasSiteTypeTests {

    @Test
    void givenWorkWithLinks_whenHasSiteType_thenReturnTrue() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .links(List.of(
              WorkLink.builder()
                  .code(SiteType.MANGADEX)
                  .build()
          ))
          .build();

      assertTrue(work.hasSiteType(SiteType.MANGADEX));
    }

    @Test
    void givenWorkWithoutRequestedSiteType_whenHasSiteType_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .links(List.of(
              WorkLink.builder()
                  .code(SiteType.MANGA_UPDATES)
                  .build()
          ))
          .build();

      assertFalse(work.hasSiteType(SiteType.MANGADEX));
    }

    @Test
    void givenWorkWithEmptyLinks_whenHasSiteType_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .links(List.of())
          .build();

      assertFalse(work.hasSiteType(SiteType.MANGADEX));
    }

    @Test
    void givenWorkWithNullLinks_whenHasSiteType_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .links(null)
          .build();

      assertFalse(work.hasSiteType(SiteType.MANGADEX));
    }
  }

  @Nested
  class HasTagWithNameOrAliasTests {

    @Test
    void givenWorkWithTagMatchingName_whenHasTagWithNameOrAlias_thenReturnTrue() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name("Action")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertTrue(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action"));
    }

    @Test
    void givenWorkWithTagMatchingAlias1_whenHasTagWithNameOrAlias_thenReturnTrue() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name("Adventure")
          .alias1("Action Adventure")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertTrue(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action adventure"));
    }

    @Test
    void givenWorkWithTagMatchingAlias2_whenHasTagWithNameOrAlias_thenReturnTrue() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name("Adventure")
          .alias2("Exploration")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertTrue(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "exploration"));
    }

    @Test
    void givenWorkWithTagMatchingAlias3_whenHasTagWithNameOrAlias_thenReturnTrue() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name("Adventure")
          .alias3("Quest")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertTrue(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "quest"));
    }

    @Test
    void givenWorkWithTagFromDifferentGroup_whenHasTagWithNameOrAlias_thenReturnFalse() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.THEME)
          .name("Action")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertFalse(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action"));
    }

    @Test
    void givenWorkWithNullTags_whenHasTagWithNameOrAlias_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(null)
          .build();

      assertFalse(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action"));
    }

    @Test
    void givenWorkWithEmptyTags_whenHasTagWithNameOrAlias_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of())
          .build();

      assertFalse(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action"));
    }

    @Test
    void givenWorkWithTagWithNullNameAndAliases_whenHasTagWithNameOrAlias_thenReturnFalse() {
      var tag = Tag.builder()
          .id(UUID.randomUUID())
          .group(TagGroupType.GENRE)
          .name(null)
          .alias1(null)
          .alias2(null)
          .alias3(null)
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .tags(List.of(
              WorkTag.builder()
                  .tag(tag)
                  .build()
          ))
          .build();

      assertFalse(work.hasTagWithNameOrAlias(TagGroupType.GENRE, "action"));
    }
  }

  @Nested
  class HasAuthorOfTypeTests {

    @Test
    void givenWorkWithAuthorOfType_whenHasAuthorOfType_thenReturnTrue() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .type(AuthorType.WRITER)
          .name("Author Name")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(List.of(
              WorkAuthor.builder()
                  .author(author)
                  .build()
          ))
          .build();

      assertTrue(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }

    @Test
    void givenWorkWithAuthorOfDifferentType_whenHasAuthorOfType_thenReturnFalse() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .type(AuthorType.ARTIST)
          .name("Author Name")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(List.of(
              WorkAuthor.builder()
                  .author(author)
                  .build()
          ))
          .build();

      assertFalse(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }

    @Test
    void givenWorkWithAuthorOfDifferentName_whenHasAuthorOfType_thenReturnFalse() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .type(AuthorType.WRITER)
          .name("Different Name")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(List.of(
              WorkAuthor.builder()
                  .author(author)
                  .build()
          ))
          .build();

      assertFalse(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }

    @Test
    void givenWorkWithNullAuthors_whenHasAuthorOfType_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(null)
          .build();

      assertFalse(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }

    @Test
    void givenWorkWithEmptyAuthors_whenHasAuthorOfType_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(List.of())
          .build();

      assertFalse(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }

    @Test
    void givenCaseInsensitive_whenHasAuthorOfType_thenReturnTrue() {
      var author = Author.builder()
          .id(UUID.randomUUID())
          .type(AuthorType.WRITER)
          .name("AUTHOR NAME")
          .build();
      var work = Work.builder()
          .id(UUID.randomUUID())
          .authors(List.of(
              WorkAuthor.builder()
                  .author(author)
                  .build()
          ))
          .build();

      assertTrue(work.hasAuthorOfType(AuthorType.WRITER, "author name"));
    }
  }

  @Nested
  class HasCoverWithOriginAndSizeTests {

    @Test
    void givenWorkWithCoverMatchingOriginAndSize_whenHasCoverWithOriginAndSize_thenReturnTrue() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .covers(List.of(
              WorkCover.builder()
                  .origin(SynchronizationOriginType.MANGADEX)
                  .size(CoverType.LOW)
                  .build()
          ))
          .build();

      assertTrue(work.hasCoverWithOriginAndSize(SynchronizationOriginType.MANGADEX, CoverType.LOW));
    }

    @Test
    void givenWorkWithCoverDifferentOrigin_whenHasCoverWithOriginAndSize_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .covers(List.of(
              WorkCover.builder()
                  .origin(SynchronizationOriginType.MEDIOCRESCAN)
                  .size(CoverType.LOW)
                  .build()
          ))
          .build();

      assertFalse(work.hasCoverWithOriginAndSize(SynchronizationOriginType.MANGADEX, CoverType.LOW));
    }

    @Test
    void givenWorkWithCoverDifferentSize_whenHasCoverWithOriginAndSize_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .covers(List.of(
              WorkCover.builder()
                  .origin(SynchronizationOriginType.MANGADEX)
                  .size(CoverType.HIGH)
                  .build()
          ))
          .build();

      assertFalse(work.hasCoverWithOriginAndSize(SynchronizationOriginType.MANGADEX, CoverType.LOW));
    }

    @Test
    void givenWorkWithNullCovers_whenHasCoverWithOriginAndSize_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .covers(null)
          .build();

      assertFalse(work.hasCoverWithOriginAndSize(SynchronizationOriginType.MANGADEX, CoverType.LOW));
    }

    @Test
    void givenWorkWithEmptyCovers_whenHasCoverWithOriginAndSize_thenReturnFalse() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .covers(List.of())
          .build();

      assertFalse(work.hasCoverWithOriginAndSize(SynchronizationOriginType.MANGADEX, CoverType.LOW));
    }
  }

  @Nested
  class GetCoverUrlTests {

    @Test
    void givenWorkWithOfficialCover_whenGetCoverUrl_thenReturnOfficialCoverUrl() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(List.of(
              WorkCover.builder()
                  .fileName("cover1.jpg")
                  .isOfficial(false)
                  .build(),
              WorkCover.builder()
                  .fileName("cover2.jpg")
                  .isOfficial(true)
                  .build()
          ))
          .build();

      String url = work.getCoverUrl();

      assertEquals("/shounen/test-work/covers/cover2.jpg", url);
    }

    @Test
    void givenWorkWithoutOfficialCover_whenGetCoverUrl_thenReturnFirstCoverUrl() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(List.of(
              WorkCover.builder()
                  .fileName("cover1.jpg")
                  .isOfficial(false)
                  .build(),
              WorkCover.builder()
                  .fileName("cover2.jpg")
                  .isOfficial(false)
                  .build()
          ))
          .build();

      String url = work.getCoverUrl();

      assertEquals("/shounen/test-work/covers/cover1.jpg", url);
    }

    @Test
    void givenWorkWithNullPublicationDemographic_whenGetCoverUrl_thenReturnUrlWithUnknown() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(null)
          .covers(List.of(
              WorkCover.builder()
                  .fileName("cover1.jpg")
                  .isOfficial(true)
                  .build()
          ))
          .build();

      String url = work.getCoverUrl();

      assertEquals("/unknown/test-work/covers/cover1.jpg", url);
    }

    @Test
    void givenWorkWithNullSlug_whenGetCoverUrl_thenReturnUrlWithUnknown() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug(null)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(List.of(
              WorkCover.builder()
                  .fileName("cover1.jpg")
                  .isOfficial(true)
                  .build()
          ))
          .build();

      String url = work.getCoverUrl();

      assertEquals("/shounen/unknown/covers/cover1.jpg", url);
    }

    @Test
    void givenWorkWithNullFileName_whenGetCoverUrl_thenReturnNull() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(List.of(
              WorkCover.builder()
                  .fileName(null)
                  .isOfficial(true)
                  .build()
          ))
          .build();

      String url = work.getCoverUrl();

      assertNull(url);
    }

    @Test
    void givenWorkWithNullCovers_whenGetCoverUrl_thenReturnNull() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(null)
          .build();

      String url = work.getCoverUrl();

      assertNull(url);
    }

    @Test
    void givenWorkWithEmptyCovers_whenGetCoverUrl_thenReturnNull() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .slug("test-work")
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .covers(List.of())
          .build();

      String url = work.getCoverUrl();

      assertNull(url);
    }
  }

  @Nested
  class BuilderTests {

    @Test
    void givenWorkBuilder_whenBuild_thenCreateWorkSuccessfully() {
      var id = UUID.randomUUID();
      var work = Work.builder()
          .id(id)
          .type(WorkType.MANHWA)
          .status(WorkStatus.ONGOING)
          .releaseYear(2020)
          .slug("test-work")
          .disabled(false)
          .build();

      assertEquals(id, work.getId());
      assertEquals(WorkType.MANHWA, work.getType());
      assertEquals(WorkStatus.ONGOING, work.getStatus());
      assertEquals(2020, work.getReleaseYear());
      assertEquals("test-work", work.getSlug());
      assertFalse(work.getDisabled());
    }
  }

  @Nested
  class EqualsAndHashCodeTests {

    @Test
    void givenTwoWorksWithIdenticalFields_whenCompared_thenAreEqual() {
      var id = UUID.randomUUID();
      var work1 = Work.builder().id(id).type(WorkType.MANHWA).status(WorkStatus.ONGOING).build();
      var work2 = Work.builder().id(id).type(WorkType.MANHWA).status(WorkStatus.ONGOING).build();

      assertEquals(work1, work2);
    }

    @Test
    void givenTwoWorksWithDifferentIds_whenCompared_thenAreNotEqual() {
      var work1 = Work.builder().id(UUID.randomUUID()).type(WorkType.MANHWA).status(WorkStatus.ONGOING).build();
      var work2 = Work.builder().id(UUID.randomUUID()).type(WorkType.MANHWA).status(WorkStatus.ONGOING).build();

      assertFalse(work1.equals(work2));
    }
  }

  @Nested
  class SerializationTests {

    @Test
    void givenWork_whenCreated_thenIsSerializable() {
      var work = Work.builder()
          .id(UUID.randomUUID())
          .type(WorkType.MANHWA)
          .status(WorkStatus.ONGOING)
          .build();

      assertTrue(work instanceof java.io.Serializable);
    }
  }
}
