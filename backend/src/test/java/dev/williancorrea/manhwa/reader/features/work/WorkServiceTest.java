package dev.williancorrea.manhwa.reader.features.work;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogOutput;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void givenNoWorks_whenFindAllWorks_thenReturnEmptyPage() {
      Pageable pageable = PageRequest.of(0, 20);
      when(repository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList()));

      Page<Work> result = workService.findAllWorks(pageable);

      assertTrue(result.isEmpty());
      verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void givenWorksExist_whenFindAllWorks_thenReturnPage() {
      Pageable pageable = PageRequest.of(0, 20);

      Work work1 = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .build();

      Work work2 = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.COMPLETED)
          .publicationDemographic(WorkPublicationDemographic.SEINEN)
          .build();

      when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(work1, work2)));

      Page<Work> result = workService.findAllWorks(pageable);

      assertEquals(2, result.getTotalElements());
      verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void givenPaginationParams_whenFindAllWorks_thenRespectPageable() {
      Pageable pageable = PageRequest.of(1, 10);
      when(repository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

      Page<Work> result = workService.findAllWorks(pageable);

      assertEquals(1, result.getNumber());
      assertEquals(10, result.getSize());
      verify(repository, times(1)).findAll(pageable);
    }
  }

  @Nested
  class WorkCatalogOutputMappingTests {

    private static final String STORAGE = "https://storage.example.com/manhwa";

    @Test
    void givenWorkWithOfficialTitle_whenFromEntity_thenUsesOfficialTitle() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .publicationDemographic(WorkPublicationDemographic.SHOUNEN)
          .titles(List.of(
              WorkTitle.builder().title("Alternative Title").isOfficial(false).build(),
              WorkTitle.builder().title("Official Title").isOfficial(true).build()
          ))
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertEquals("Official Title", output.titulo());
      assertEquals("SHOUNEN", output.demografia());
      assertEquals("ONGOING", output.status());
      assertEquals(0L, output.quantidadeCapitulos());
    }

    @Test
    void givenWorkWithNoOfficialTitle_whenFromEntity_thenUsesFirstTitle() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.COMPLETED)
          .titles(List.of(
              WorkTitle.builder().title("First Title").isOfficial(false).build(),
              WorkTitle.builder().title("Second Title").isOfficial(false).build()
          ))
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertEquals("First Title", output.titulo());
    }

    @Test
    void givenWorkWithNoTitles_whenFromEntity_thenTituloIsNull() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .titles(Collections.emptyList())
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertNull(output.titulo());
    }

    @Test
    void givenWorkWithNoDemographic_whenFromEntity_thenDemografiaIsNull() {
      Work work = Work.builder()
          .id(UUID.randomUUID())
          .status(WorkStatus.ONGOING)
          .publicationDemographic(null)
          .titles(Collections.emptyList())
          .covers(Collections.emptyList())
          .build();

      WorkCatalogOutput output = WorkCatalogOutput.fromEntity(work, STORAGE);

      assertNull(output.demografia());
    }
  }
}
