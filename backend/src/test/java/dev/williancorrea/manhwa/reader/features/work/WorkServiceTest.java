package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
