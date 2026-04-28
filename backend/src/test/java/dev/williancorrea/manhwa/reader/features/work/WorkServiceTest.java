package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogOutput;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

class WorkServiceTest {

  @Mock
  private WorkRepository repository;

  @Mock
  private ScraperHelper scraperHelper;

  @InjectMocks
  private WorkService workService;

  @SuppressWarnings("resource")
  public WorkServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class FindAllWorksTests {

    static Stream<Arguments> sortParams() {
      return Stream.of(
          Arguments.of(null, Sort.Direction.DESC),
          Arguments.of("updated_at_desc", Sort.Direction.DESC),
          Arguments.of("updated_at_asc", Sort.Direction.ASC),
          Arguments.of("invalid_sort", Sort.Direction.DESC)
      );
    }

    @ParameterizedTest(name = "sort=''{0}'' -> updatedAt {1}")
    @MethodSource("sortParams")
    void givenSortParam_whenFindAllWorks_thenResolvesCorrectUpdatedAtSort(String sortParam, Sort.Direction expectedDirection) {
      var filter = new WorkCatalogFilter(null, null, null, null, sortParam);
      var pageable = PageRequest.of(0, 20);
      when(repository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList()));

      workService.findAllWorks(filter, pageable);

      var captor = ArgumentCaptor.forClass(Pageable.class);
      verify(repository, times(1)).findAll(any(Specification.class), captor.capture());
      Sort.Order order = captor.getValue().getSort().getOrderFor("updatedAt");
      assertTrue(order != null && order.getDirection() == expectedDirection);
    }

    @Test
    void givenPageableParams_whenFindAllWorks_thenPreservesPageAndSize() {
      var filter = new WorkCatalogFilter(null, null, null, null, null);
      var pageable = PageRequest.of(2, 15);
      when(repository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

      workService.findAllWorks(filter, pageable);

      var captor = ArgumentCaptor.forClass(Pageable.class);
      verify(repository, times(1)).findAll(any(Specification.class), captor.capture());
      assertEquals(2, captor.getValue().getPageNumber());
      assertEquals(15, captor.getValue().getPageSize());
    }

    @Test
    void givenNoWorks_whenFindAllWorks_thenReturnEmptyPage() {
      var filter = new WorkCatalogFilter(null, null, null, null, null);
      when(repository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList()));

      Page<Work> result = workService.findAllWorks(filter, PageRequest.of(0, 20));

      assertTrue(result.isEmpty());
    }

    @Test
    void givenWorksExist_whenFindAllWorks_thenReturnAllInPage() {
      var filter = new WorkCatalogFilter(null, null, null, null, null);
      var work1 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      var work2 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.COMPLETED).build();
      when(repository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(work1, work2)));

      Page<Work> result = workService.findAllWorks(filter, PageRequest.of(0, 20));

      assertEquals(2, result.getTotalElements());
    }
  }

  @Nested
  class WorkCatalogOutputMappingTests {

    private static final String STORAGE = "https://storage.example.com/manhwa";

    static Stream<Arguments> titleResolutionCases() {
      return Stream.of(
          Arguments.of(
              "official title exists",
              List.of(
                  WorkTitle.builder().title("Alternative Title").isOfficial(false).build(),
                  WorkTitle.builder().title("Official Title").isOfficial(true).build()
              ),
              "Official Title"
          ),
          Arguments.of(
              "no official title",
              List.of(
                  WorkTitle.builder().title("First Title").isOfficial(false).build(),
                  WorkTitle.builder().title("Second Title").isOfficial(false).build()
              ),
              "First Title"
          ),
          Arguments.of(
              "no titles",
              Collections.emptyList(),
              null
          )
      );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("titleResolutionCases")
    void givenTitleList_whenFromEntity_thenResolvesExpectedTitle(String description, List<WorkTitle> titles, String expectedTitle) {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .titles(titles)
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertEquals(expectedTitle, output.title());
    }

    @Test
    void givenWorkWithAllFields_whenFromEntity_thenMapsCorrectly() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .titles(List.of(WorkTitle.builder().title("Official Title").isOfficial(true).build()))
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertEquals("Official Title", output.title());
      assertEquals("SHOUNEN", output.publicationDemographic());
      assertEquals("ONGOING", output.status());
      assertEquals(0L, output.chapterCount());
    }

    @Test
    void givenWorkWithNullDemographic_whenFromEntity_thenPublicationDemographicIsNull() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .publicationDemographic(null)
          .titles(Collections.emptyList())
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertNull(output.publicationDemographic());
    }

    @Test
    void givenWorkWithNullStatus_whenFromEntity_thenStatusIsNull() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .titles(Collections.emptyList())
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertNull(output.status());
    }

    @Test
    void givenWorkWithNullChapterCount_whenFromEntity_thenChapterCountIsZero() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .titles(Collections.emptyList())
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertEquals(0L, output.chapterCount());
    }
  }

  @Nested
  class FindAllTests {

    @Test
    void givenNoWorks_whenFindAll_thenReturnEmptyList() {
      when(repository.findAll()).thenReturn(Collections.emptyList());

      List<Work> result = workService.findAll();

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAll();
    }

    @Test
    void givenWorksExist_whenFindAll_thenReturnAllWorks() {
      var work1 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      var work2 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.COMPLETED).build();
      when(repository.findAll()).thenReturn(List.of(work1, work2));

      List<Work> result = workService.findAll();

      assertEquals(2, result.size());
      verify(repository, times(1)).findAll();
    }
  }

  @Nested
  class FindByIdTests {

    @Test
    void givenWorkExists_whenFindById_thenReturnWork() {
      var id = UUID.randomUUID();
      var work = Work.builder().id(id).status(WorkStatus.ONGOING).build();
      when(repository.findById(id)).thenReturn(java.util.Optional.of(work));

      var result = workService.findById(id);

      assertTrue(result.isPresent());
      assertEquals(id, result.get().getId());
      verify(repository, times(1)).findById(id);
    }

    @Test
    void givenWorkDoesNotExist_whenFindById_thenReturnEmpty() {
      var id = UUID.randomUUID();
      when(repository.findById(id)).thenReturn(java.util.Optional.empty());

      var result = workService.findById(id);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findById(id);
    }
  }

  @Nested
  class SaveAndNotifyIfNewTests {

    @Test
    void givenNewWork_whenSaveAndNotifyIfNew_thenNotifyScraperHelper() {
      var work = Work.builder().status(WorkStatus.ONGOING).build();
      var savedWork = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.saveAndFlush(work)).thenReturn(savedWork);

      Work result = workService.saveAndNotifyIfNew(work, dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);

      assertEquals(savedWork.getId(), result.getId());
      verify(repository, times(1)).saveAndFlush(work);
      verify(scraperHelper, times(1)).notifyWorkAdded(savedWork, dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);
    }

    @Test
    void givenExistingWork_whenSaveAndNotifyIfNew_thenDoNotNotifyScraperHelper() {
      var work = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.saveAndFlush(work)).thenReturn(work);

      Work result = workService.saveAndNotifyIfNew(work, dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);

      assertEquals(work.getId(), result.getId());
      verify(repository, times(1)).saveAndFlush(work);
      verify(scraperHelper, times(0)).notifyWorkAdded(any(), any());
    }
  }

  @Nested
  class SaveTests {

    @Test
    void givenWork_whenSave_thenSaveToRepository() {
      var work = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.saveAndFlush(work)).thenReturn(work);

      Work result = workService.save(work);

      assertEquals(work.getId(), result.getId());
      verify(repository, times(1)).saveAndFlush(work);
    }
  }

  @Nested
  class ExistsByIdTests {

    @Test
    void givenWorkExists_whenExistsById_thenReturnTrue() {
      var id = UUID.randomUUID();
      when(repository.existsById(id)).thenReturn(true);

      boolean result = workService.existsById(id);

      assertTrue(result);
      verify(repository, times(1)).existsById(id);
    }

    @Test
    void givenWorkDoesNotExist_whenExistsById_thenReturnFalse() {
      var id = UUID.randomUUID();
      when(repository.existsById(id)).thenReturn(false);

      boolean result = workService.existsById(id);

      assertTrue(!result);
      verify(repository, times(1)).existsById(id);
    }
  }

  @Nested
  class DeleteByIdTests {

    @Test
    void givenWorkId_whenDeleteById_thenCallRepository() {
      var id = UUID.randomUUID();

      workService.deleteById(id);

      verify(repository, times(1)).deleteById(id);
    }
  }

  @Nested
  class FindAllByStatusTests {

    @Test
    void givenWorksWithStatus_whenFindAllByStatus_thenReturnWorks() {
      var work1 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      var work2 = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.findAllByStatus(WorkStatus.ONGOING)).thenReturn(List.of(work1, work2));

      List<Work> result = workService.findAllByStatus(WorkStatus.ONGOING);

      assertEquals(2, result.size());
      verify(repository, times(1)).findAllByStatus(WorkStatus.ONGOING);
    }

    @Test
    void givenNoWorksWithStatus_whenFindAllByStatus_thenReturnEmptyList() {
      when(repository.findAllByStatus(WorkStatus.COMPLETED)).thenReturn(Collections.emptyList());

      List<Work> result = workService.findAllByStatus(WorkStatus.COMPLETED);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAllByStatus(WorkStatus.COMPLETED);
    }
  }

  @Nested
  class FindAllByTypeTests {

    @Test
    void givenWorksWithType_whenFindAllByType_thenReturnWorks() {
      var work1 = Work.builder().id(UUID.randomUUID()).type(WorkType.MANHWA).build();
      var work2 = Work.builder().id(UUID.randomUUID()).type(WorkType.MANHWA).build();
      when(repository.findAllByType(WorkType.MANHWA)).thenReturn(List.of(work1, work2));

      List<Work> result = workService.findAllByType(WorkType.MANHWA);

      assertEquals(2, result.size());
      verify(repository, times(1)).findAllByType(WorkType.MANHWA);
    }

    @Test
    void givenNoWorksWithType_whenFindAllByType_thenReturnEmptyList() {
      when(repository.findAllByType(WorkType.MANGA)).thenReturn(Collections.emptyList());

      List<Work> result = workService.findAllByType(WorkType.MANGA);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAllByType(WorkType.MANGA);
    }
  }

  @Nested
  class FindByTitleTests {

    @Test
    void givenWorkWithTitle_whenFindByTitle_thenReturnWork() {
      var work = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.findByTitle("Test Title")).thenReturn(java.util.Optional.of(work));

      var result = workService.findByTitle("Test Title");

      assertTrue(result.isPresent());
      verify(repository, times(1)).findByTitle("Test Title");
    }

    @Test
    void givenNoWorkWithTitle_whenFindByTitle_thenReturnEmpty() {
      when(repository.findByTitle("Non Existent Title")).thenReturn(java.util.Optional.empty());

      var result = workService.findByTitle("Non Existent Title");

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findByTitle("Non Existent Title");
    }
  }

  @Nested
  class FindBySynchronizationExternalIDTests {

    @Test
    void givenWorkWithExternalId_whenFindBySynchronizationExternalID_thenReturnWork() {
      var work = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      when(repository.findBySynchronizationExternalID("external-123", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX))
          .thenReturn(java.util.Optional.of(work));

      var result = workService.findBySynchronizationExternalID("external-123", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);

      assertTrue(result.isPresent());
      verify(repository, times(1)).findBySynchronizationExternalID("external-123", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);
    }

    @Test
    void givenNoWorkWithExternalId_whenFindBySynchronizationExternalID_thenReturnEmpty() {
      when(repository.findBySynchronizationExternalID("non-existent", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX))
          .thenReturn(java.util.Optional.empty());

      var result = workService.findBySynchronizationExternalID("non-existent", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findBySynchronizationExternalID("non-existent", dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType.MANGADEX);
    }
  }

  @Nested
  class FindBySlugTests {

    @Test
    void givenWorkWithSlug_whenFindBySlug_thenReturnWork() {
      var work = Work.builder().id(UUID.randomUUID()).slug("test-slug").build();
      when(repository.findBySlug("test-slug")).thenReturn(java.util.Optional.of(work));

      var result = workService.findBySlug("test-slug");

      assertTrue(result.isPresent());
      assertEquals("test-slug", result.get().getSlug());
      verify(repository, times(1)).findBySlug("test-slug");
    }

    @Test
    void givenNoWorkWithSlug_whenFindBySlug_thenReturnEmpty() {
      when(repository.findBySlug("non-existent-slug")).thenReturn(java.util.Optional.empty());

      var result = workService.findBySlug("non-existent-slug");

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findBySlug("non-existent-slug");
    }
  }

  @Nested
  class FindBySlugWithDetailsTests {

    @Test
    void givenWorkWithSlug_whenFindBySlugWithDetails_thenReturnWorkWithDetails() {
      var work = Work.builder().id(UUID.randomUUID()).slug("test-slug").build();
      when(repository.findBySlugWithDetails("test-slug")).thenReturn(java.util.Optional.of(work));

      var result = workService.findBySlugWithDetails("test-slug");

      assertTrue(result.isPresent());
      assertEquals("test-slug", result.get().getSlug());
      verify(repository, times(1)).findBySlugWithDetails("test-slug");
    }

    @Test
    void givenNoWorkWithSlug_whenFindBySlugWithDetails_thenReturnEmpty() {
      when(repository.findBySlugWithDetails("non-existent-slug")).thenReturn(java.util.Optional.empty());

      var result = workService.findBySlugWithDetails("non-existent-slug");

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findBySlugWithDetails("non-existent-slug");
    }
  }

  @Nested
  class FindAllWithSpecificationTests {

    @Test
    void givenWorksMatchingSpecification_whenFindAll_thenReturnPage() {
      var work = Work.builder().id(UUID.randomUUID()).status(WorkStatus.ONGOING).build();
      var pageable = PageRequest.of(0, 20);
      var page = new PageImpl<Work>(List.of(work), pageable, 1);
      when(repository.findAll(ArgumentMatchers.<Specification<Work>>any(), any(Pageable.class))).thenReturn(page);

      Page<Work> result = workService.findAll(null, pageable);

      assertEquals(1, result.getTotalElements());
      verify(repository, times(1)).findAll(ArgumentMatchers.<Specification<Work>>any(), any(Pageable.class));
    }

    @Test
    void givenNoWorksMatchingSpecification_whenFindAll_thenReturnEmptyPage() {
      var pageable = PageRequest.of(0, 20);
      var page = new PageImpl<Work>(Collections.emptyList(), pageable, 0);
      when(repository.findAll(ArgumentMatchers.<Specification<Work>>any(), any(Pageable.class))).thenReturn(page);

      Page<Work> result = workService.findAll(null, pageable);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAll(ArgumentMatchers.<Specification<Work>>any(), any(Pageable.class));
    }
  }
}
