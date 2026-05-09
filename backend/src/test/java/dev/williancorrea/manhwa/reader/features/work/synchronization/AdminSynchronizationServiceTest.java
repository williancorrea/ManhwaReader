package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.scraper.mangadex.MangaDexApiService;
import dev.williancorrea.manhwa.reader.scraper.mangadex.client.MangaDexClient;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexAttributes;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexData;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexResponseList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminSynchronizationService")
class AdminSynchronizationServiceTest {

  @Mock
  private WorkService workService;

  @Mock
  private MangaDexApiService mangaDexApiService;

  @Mock
  private MangaDexClient mangaDexClient;

  @InjectMocks
  private AdminSynchronizationService service;

  private UUID workId;
  private UUID syncId;
  private Work testWork;
  private String minioUrl;
  private String bucketName;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    syncId = UUID.randomUUID();
    minioUrl = "https://minio.example.com";
    bucketName = "covers";
    testWork = Work.builder()
        .id(workId)
        .slug("test-work")
        .build();

    ReflectionTestUtils.setField(service, "minioUrl", minioUrl);
    ReflectionTestUtils.setField(service, "bucketName", bucketName);
  }

  @Nested
  @DisplayName("listWorks()")
  class ListWorksTests {

    @Test
    @DisplayName("should list all works with pagination")
    void shouldListAllWorksWithPagination() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(testWork), pageable, 1);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks(null, null, pageable);

      assertThat(result).isNotNull();
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getTotalElements()).isEqualTo(1);
      verify(workService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should filter works by title")
    void shouldFilterWorksByTitle() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(testWork), pageable, 1);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks("test", null, pageable);

      assertThat(result).isNotNull();
      verify(workService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should filter works linked to MangaDex")
    void shouldFilterWorksLinkedToMangaDex() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(testWork), pageable, 1);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks(null, true, pageable);

      assertThat(result).isNotNull();
      verify(workService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should filter works not linked to MangaDex")
    void shouldFilterWorksNotLinkedToMangaDex() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(testWork), pageable, 1);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks(null, false, pageable);

      assertThat(result).isNotNull();
      verify(workService, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("should map Work entities to AdminWorkOutput")
    void shouldMapWorkEntitiesToAdminWorkOutput() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(testWork), pageable, 1);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks(null, null, pageable);

      assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("should return empty page when no works found")
    void shouldReturnEmptyPageWhenNoWorksFound() {
      var pageable = PageRequest.of(0, 20);
      var worksPage = new PageImpl<>(List.of(), pageable, 0);
      when(workService.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(worksPage);

      var result = service.listWorks(null, null, pageable);

      assertThat(result).isNotNull();
      assertThat(result.getContent()).isEmpty();
      assertThat(result.getTotalElements()).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("searchMangaDex()")
  class SearchMangaDexTests {

    @Test
    @DisplayName("should search MangaDex by title")
    void shouldSearchMangaDexByTitle() {
      var mangaDexData = new MangaDexData();
      mangaDexData.setId("manga-123");
      var attributes = new MangaDexAttributes();
      attributes.setTitle(java.util.Map.of("en", "Manga Title"));
      mangaDexData.setAttributes(attributes);

      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of(mangaDexData));

      when(mangaDexClient.searchManga(eq("test"), eq(5), eq(0), anyList()))
          .thenReturn(response);

      var result = service.searchMangaDex("test", 0, 5);

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().id()).isEqualTo("manga-123");
      verify(mangaDexClient, times(1)).searchManga(eq("test"), eq(5), eq(0), anyList());
    }

    @Test
    @DisplayName("should trim title before searching")
    void shouldTrimTitleBeforeSearching() {
      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of());

      when(mangaDexClient.searchManga(eq("test"), any(), any(), anyList()))
          .thenReturn(response);

      service.searchMangaDex("  test  ", 0, 5);

      var captor = ArgumentCaptor.forClass(String.class);
      verify(mangaDexClient).searchManga(captor.capture(), any(), any(), anyList());
      assertThat(captor.getValue()).isEqualTo("test");
    }

    @Test
    @DisplayName("should handle null title")
    void shouldHandleNullTitle() {
      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of());

      when(mangaDexClient.searchManga(eq(null), any(), any(), anyList()))
          .thenReturn(response);

      var result = service.searchMangaDex(null, 0, 5);

      assertThat(result).isEmpty();
      verify(mangaDexClient).searchManga(eq(null), any(), any(), anyList());
    }

    @Test
    @DisplayName("should calculate correct offset")
    void shouldCalculateCorrectOffset() {
      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of());

      when(mangaDexClient.searchManga(any(), eq(10), eq(30), anyList()))
          .thenReturn(response);

      service.searchMangaDex("test", 3, 10);

      verify(mangaDexClient).searchManga(any(), eq(10), eq(30), anyList());
    }

    @Test
    @DisplayName("should return empty list when response result is not ok")
    void shouldReturnEmptyListWhenResponseResultIsNotOk() {
      var response = new MangaDexResponseList();
      response.setResult("error");
      response.setData(List.of());

      when(mangaDexClient.searchManga(any(), any(), any(), anyList()))
          .thenReturn(response);

      var result = service.searchMangaDex("test", 0, 5);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return empty list when data is null")
    void shouldReturnEmptyListWhenDataIsNull() {
      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(null);

      when(mangaDexClient.searchManga(any(), any(), any(), anyList()))
          .thenReturn(response);

      var result = service.searchMangaDex("test", 0, 5);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should map MangaDexData to MangaDexSearchOutput")
    void shouldMapMangaDexDataToMangaDexSearchOutput() {
      var mangaDexData = new MangaDexData();
      mangaDexData.setId("manga-123");
      var attributes = new MangaDexAttributes();
      attributes.setTitle(java.util.Map.of("en", "Title"));
      mangaDexData.setAttributes(attributes);

      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of(mangaDexData));

      when(mangaDexClient.searchManga(any(), any(), any(), anyList()))
          .thenReturn(response);

      var result = service.searchMangaDex("test", 0, 5);

      assertThat(result.getFirst().id()).isEqualTo("manga-123");
    }

    @Test
    @DisplayName("should request specific relationships from MangaDex")
    void shouldRequestSpecificRelationshipsFromMangaDex() {
      var response = new MangaDexResponseList();
      response.setResult("ok");
      response.setData(List.of());

      when(mangaDexClient.searchManga(any(), any(), any(), anyList()))
          .thenReturn(response);

      service.searchMangaDex("test", 0, 5);

      var captor = ArgumentCaptor.forClass(List.class);
      verify(mangaDexClient).searchManga(any(), any(), any(), captor.capture());
      assertThat(captor.getValue())
          .contains("artist", "author", "cover_art");
    }
  }

  @Nested
  @DisplayName("linkWorkToMangaDex()")
  class LinkWorkToMangaDexTests {

    @Test
    @DisplayName("should link work to MangaDex when not already linked")
    void shouldLinkWorkToMangaDexWhenNotAlreadyLinked() {
      testWork.setSynchronizations(new ArrayList<>());
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));
      when(workService.save(testWork)).thenReturn(testWork);

      service.linkWorkToMangaDex(workId, "manga-123");

      assertThat(testWork.getSynchronizations()).hasSize(1);
      assertThat(testWork.getSynchronizations().getFirst().getOrigin())
          .isEqualTo(SynchronizationOriginType.MANGADEX);
      assertThat(testWork.getSynchronizations().getFirst().getExternalId())
          .isEqualTo("manga-123");
      verify(workService).save(testWork);
    }

    @Test
    @DisplayName("should create synchronizations list if null")
    void shouldCreateSynchronizationsListIfNull() {
      testWork.setSynchronizations(null);
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));
      when(workService.save(testWork)).thenReturn(testWork);

      service.linkWorkToMangaDex(workId, "manga-123");

      assertThat(testWork.getSynchronizations()).isNotNull().hasSize(1);
      verify(workService).save(testWork);
    }

    @Test
    @DisplayName("should throw BusinessException when work not found")
    void shouldThrowBusinessExceptionWhenWorkNotFound() {
      when(workService.findById(workId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.linkWorkToMangaDex(workId, "manga-123"))
          .isInstanceOf(BusinessException.class);
      verify(workService, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw ConflictException when work already linked to MangaDex")
    void shouldThrowConflictExceptionWhenWorkAlreadyLinked() {
      var sync = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("existing-id")
          .build();
      testWork.setSynchronizations(List.of(sync));
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));

      assertThatThrownBy(() -> service.linkWorkToMangaDex(workId, "manga-123"))
          .isInstanceOf(ConflictException.class);
      verify(workService, times(0)).save(any());
    }

    @Test
    @DisplayName("should trigger MangaDex API synchronization after linking")
    void shouldTriggerMangaDexApiSynchronizationAfterLinking() {
      testWork.setSynchronizations(new ArrayList<>());
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));
      when(workService.save(testWork)).thenReturn(testWork);

      service.linkWorkToMangaDex(workId, "manga-123");

      verify(mangaDexApiService).searchMangaByIDFromExternalApi("manga-123");
    }

    @Test
    @DisplayName("should add synchronization to existing list")
    void shouldAddSynchronizationToExistingList() {
      var existingSync = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.LYCANTOONS)
          .externalId("existing-id")
          .build();
      testWork.setSynchronizations(new ArrayList<>(List.of(existingSync)));
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));
      when(workService.save(testWork)).thenReturn(testWork);

      service.linkWorkToMangaDex(workId, "manga-123");

      assertThat(testWork.getSynchronizations()).hasSize(2);
      assertThat(testWork.getSynchronizations())
          .anySatisfy(s -> assertThat(s.getOrigin()).isEqualTo(SynchronizationOriginType.MANGADEX));
    }
  }

  @Nested
  @DisplayName("syncWorkWithMangaDex()")
  class SyncWorkWithMangaDexTests {

    @Test
    @DisplayName("should sync work with MangaDex when linked")
    void shouldSyncWorkWithMangaDexWhenLinked() {
      var sync = WorkSynchronization.builder()
          .id(syncId)
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("manga-123")
          .build();
      testWork.setSynchronizations(List.of(sync));
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));

      service.syncWorkWithMangaDex(workId);

      verify(mangaDexApiService).searchMangaByIDFromExternalApi("manga-123");
    }

    @Test
    @DisplayName("should throw BusinessException when work not found")
    void shouldThrowBusinessExceptionWhenWorkNotFound() {
      when(workService.findById(workId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.syncWorkWithMangaDex(workId))
          .isInstanceOf(BusinessException.class);
      verify(mangaDexApiService, times(0)).searchMangaByIDFromExternalApi(any());
    }

    @Test
    @DisplayName("should throw BusinessException when work not linked to MangaDex")
    void shouldThrowBusinessExceptionWhenWorkNotLinked() {
      var sync = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.LYCANTOONS)
          .externalId("external-id")
          .build();
      testWork.setSynchronizations(List.of(sync));
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));

      assertThatThrownBy(() -> service.syncWorkWithMangaDex(workId))
          .isInstanceOf(BusinessException.class);
      verify(mangaDexApiService, times(0)).searchMangaByIDFromExternalApi(any());
    }

    @Test
    @DisplayName("should use correct external ID from synchronization")
    void shouldUseCorrectExternalIdFromSynchronization() {
      var sync1 = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.LYCANTOONS)
          .externalId("lycantoons-id")
          .build();
      var sync2 = WorkSynchronization.builder()
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("manga-456")
          .build();
      testWork.setSynchronizations(List.of(sync1, sync2));
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));

      service.syncWorkWithMangaDex(workId);

      verify(mangaDexApiService).searchMangaByIDFromExternalApi("manga-456");
    }

    @Test
    @DisplayName("should handle empty synchronizations list")
    void shouldHandleEmptySynchronizationsList() {
      testWork.setSynchronizations(new ArrayList<>());
      when(workService.findById(workId)).thenReturn(Optional.of(testWork));

      assertThatThrownBy(() -> service.syncWorkWithMangaDex(workId))
          .isInstanceOf(BusinessException.class);
    }
  }
}
