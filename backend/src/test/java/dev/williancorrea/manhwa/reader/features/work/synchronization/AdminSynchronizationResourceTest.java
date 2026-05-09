package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminSynchronizationResource")
class AdminSynchronizationResourceTest {

  @Mock
  private AdminSynchronizationService service;

  @InjectMocks
  private AdminSynchronizationResource resource;

  private UUID workId;
  private UUID workId2;

  @BeforeEach
  void setUp() {
    workId = UUID.randomUUID();
    workId2 = UUID.randomUUID();
  }

  @Nested
  @DisplayName("listWorks()")
  class ListWorksTests {

    @Test
    @DisplayName("should return page of works with default parameters")
    void shouldReturnPageOfWorksWithDefaultParameters() {
      var output = new AdminWorkOutput(
          workId,
          "Test Work",
          List.of("Test Work"),
          "test-work",
          "ONGOING",
          "MANGA",
          "SHOUNEN",
          null,
          List.of(),
          List.of()
      );
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(output), PageRequest.of(0, 20), 1);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
      verify(service).listWorks(null, null, PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("should enforce max size of 50")
    void shouldEnforceMaxSizeOf50() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 50), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 50)))
          .thenReturn(page);

      resource.listWorks(0, 100, null, null);

      verify(service).listWorks(null, null, PageRequest.of(0, 50));
    }

    @Test
    @DisplayName("should pass title filter to service")
    void shouldPassTitleFilterToService() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks("test title", null, PageRequest.of(0, 20)))
          .thenReturn(page);

      resource.listWorks(0, 20, "test title", null);

      verify(service).listWorks("test title", null, PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("should pass linkedToMangaDex filter to service")
    void shouldPassLinkedToMangaDexFilterToService() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, true, PageRequest.of(0, 20)))
          .thenReturn(page);

      resource.listWorks(0, 20, null, true);

      verify(service).listWorks(null, true, PageRequest.of(0, 20));
    }

    @Test
    @DisplayName("should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(2, 25), 0);
      when(service.listWorks(null, null, PageRequest.of(2, 25)))
          .thenReturn(page);

      resource.listWorks(2, 25, null, null);

      verify(service).listWorks(null, null, PageRequest.of(2, 25));
    }

    @Test
    @DisplayName("should return OK status")
    void shouldReturnOkStatus() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }

  @Nested
  @DisplayName("searchMangaDex()")
  class SearchMangaDexTests {

    @Test
    @DisplayName("should search MangaDex with provided title")
    void shouldSearchMangaDexWithProvidedTitle() {
      var searchOutput = new MangaDexSearchOutput("id", "url", List.of("Title"));
      when(service.searchMangaDex("test manga", 0, 5))
          .thenReturn(List.of(searchOutput));

      var response = resource.searchMangaDex("test manga", 0, 5);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).hasSize(1);
      verify(service).searchMangaDex("test manga", 0, 5);
    }

    @Test
    @DisplayName("should enforce max size of 20")
    void shouldEnforceMaxSizeOf20() {
      when(service.searchMangaDex("test", 0, 20))
          .thenReturn(List.of());

      resource.searchMangaDex("test", 0, 50);

      verify(service).searchMangaDex("test", 0, 20);
    }

    @Test
    @DisplayName("should use default page 0")
    void shouldUseDefaultPage0() {
      when(service.searchMangaDex("test", 0, 5))
          .thenReturn(List.of());

      resource.searchMangaDex("test", 0, 5);

      verify(service).searchMangaDex("test", 0, 5);
    }

    @Test
    @DisplayName("should limit size to 20 when larger provided")
    void shouldLimitSizeTo20() {
      when(service.searchMangaDex("test", 0, 20))
          .thenReturn(List.of());

      resource.searchMangaDex("test", 0, 25);

      verify(service).searchMangaDex("test", 0, 20);
    }

    @Test
    @DisplayName("should calculate offset correctly")
    void shouldCalculateOffsetCorrectly() {
      when(service.searchMangaDex("test", 2, 10))
          .thenReturn(List.of());

      resource.searchMangaDex("test", 2, 10);

      verify(service).searchMangaDex("test", 2, 10);
    }

    @Test
    @DisplayName("should return OK status")
    void shouldReturnOkStatus() {
      when(service.searchMangaDex(anyString(), anyInt(), anyInt()))
          .thenReturn(List.of());

      var response = resource.searchMangaDex("test", 0, 5);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should return empty list when no results")
    void shouldReturnEmptyListWhenNoResults() {
      when(service.searchMangaDex("nonexistent", 0, 5))
          .thenReturn(List.of());

      var response = resource.searchMangaDex("nonexistent", 0, 5);

      assertThat(response.getBody()).isEmpty();
    }
  }

  @Nested
  @DisplayName("linkWorkToMangaDex()")
  class LinkWorkToMangaDexTests {

    @Test
    @DisplayName("should link work to MangaDex")
    void shouldLinkWorkToMangaDex() {
      var input = new LinkWorkInput(workId, "manga-123");

      var response = resource.linkWorkToMangaDex(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      verify(service).linkWorkToMangaDex(workId, "manga-123");
    }

    @Test
    @DisplayName("should return CREATED status")
    void shouldReturnCreatedStatus() {
      var input = new LinkWorkInput(workId, "manga-123");

      var response = resource.linkWorkToMangaDex(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("should return empty body on success")
    void shouldReturnEmptyBodyOnSuccess() {
      var input = new LinkWorkInput(workId, "manga-123");

      var response = resource.linkWorkToMangaDex(input);

      assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("should extract workId and mangaDexId from input")
    void shouldExtractWorkIdAndMangaDexIdFromInput() {
      var input = new LinkWorkInput(workId2, "manga-456");

      resource.linkWorkToMangaDex(input);

      verify(service).linkWorkToMangaDex(workId2, "manga-456");
    }

    @Test
    @DisplayName("should handle different mangaDex IDs")
    void shouldHandleDifferentMangaDexIds() {
      var input1 = new LinkWorkInput(workId, "manga-abc");
      var input2 = new LinkWorkInput(workId, "manga-xyz");

      resource.linkWorkToMangaDex(input1);
      resource.linkWorkToMangaDex(input2);

      verify(service).linkWorkToMangaDex(workId, "manga-abc");
      verify(service).linkWorkToMangaDex(workId, "manga-xyz");
    }
  }

  @Nested
  @DisplayName("syncWorkWithMangaDex()")
  class SyncWorkWithMangaDexTests {

    @Test
    @DisplayName("should sync work with MangaDex")
    void shouldSyncWorkWithMangaDex() {
      var response = resource.syncWorkWithMangaDex(workId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(service).syncWorkWithMangaDex(workId);
    }

    @Test
    @DisplayName("should return OK status")
    void shouldReturnOkStatus() {
      var response = resource.syncWorkWithMangaDex(workId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should return empty body on success")
    void shouldReturnEmptyBodyOnSuccess() {
      var response = resource.syncWorkWithMangaDex(workId);

      assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("should pass workId to service")
    void shouldPassWorkIdToService() {
      resource.syncWorkWithMangaDex(workId2);

      verify(service).syncWorkWithMangaDex(workId2);
    }

    @Test
    @DisplayName("should handle multiple sync requests")
    void shouldHandleMultipleSyncRequests() {
      resource.syncWorkWithMangaDex(workId);
      resource.syncWorkWithMangaDex(workId2);

      verify(service).syncWorkWithMangaDex(workId);
      verify(service).syncWorkWithMangaDex(workId2);
    }
  }

  @Nested
  @DisplayName("HTTP status codes")
  class HttpStatusCodesTests {

    @Test
    @DisplayName("listWorks should return 200 OK")
    void listWorksShouldReturn200Ok() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("searchMangaDex should return 200 OK")
    void searchMangaDexShouldReturn200Ok() {
      when(service.searchMangaDex(anyString(), anyInt(), anyInt()))
          .thenReturn(List.of());

      var response = resource.searchMangaDex("test", 0, 5);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("linkWorkToMangaDex should return 201 CREATED")
    void linkWorkToMangaDexShouldReturn201Created() {
      var input = new LinkWorkInput(workId, "manga-123");

      var response = resource.linkWorkToMangaDex(input);

      assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    @DisplayName("syncWorkWithMangaDex should return 200 OK")
    void syncWorkWithMangaDexShouldReturn200Ok() {
      var response = resource.syncWorkWithMangaDex(workId);

      assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
  }

  @Nested
  @DisplayName("parameter validation")
  class ParameterValidationTests {

    @Test
    @DisplayName("listWorks should handle page 0")
    void listWorksShouldHandlePage0() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("listWorks should handle large page numbers")
    void listWorksShouldHandleLargePageNumbers() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(99, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(99, 20)))
          .thenReturn(page);

      var response = resource.listWorks(99, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("listWorks should handle size 1")
    void listWorksShouldHandleSize1() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 1), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 1)))
          .thenReturn(page);

      var response = resource.listWorks(0, 1, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }

  @Nested
  @DisplayName("null/optional parameters")
  class NullOptionalParametersTests {

    @Test
    @DisplayName("listWorks should handle null title")
    void listWorksShouldHandleNullTitle() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("listWorks should handle null linkedToMangaDex")
    void listWorksShouldHandleNullLinkedToMangaDex() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, null, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, null);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("listWorks should handle false linkedToMangaDex")
    void listWorksShouldHandleFalseLinkedToMangaDex() {
      Page<AdminWorkOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
      when(service.listWorks(null, false, PageRequest.of(0, 20)))
          .thenReturn(page);

      var response = resource.listWorks(0, 20, null, false);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }
}
