package dev.williancorrea.manhwa.reader.features.library;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.library.dto.LibraryItemOutput;
import dev.williancorrea.manhwa.reader.features.work.Work;
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
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibraryService")
class LibraryServiceTest {

  @Mock
  private LibraryRepository libraryRepository;

  @InjectMocks
  private LibraryService libraryService;

  private UUID libraryId;
  private UUID userId;
  private UUID workId;
  private Library library;
  private User user;
  private Work work;
  private String storageBaseUrl;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    userId = UUID.randomUUID();
    workId = UUID.randomUUID();
    storageBaseUrl = "https://storage.example.com/bucket";

    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();

    work = Work.builder()
        .id(workId)
        .slug("test-slug")
        .build();

    library = Library.builder()
        .id(libraryId)
        .user(user)
        .work(work)
        .status(LibraryStatus.READING)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all libraries")
    void shouldReturnAllLibraries() {
      var library2 = Library.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .status(LibraryStatus.COMPLETED)
          .build();
      var libraries = List.of(library, library2);

      when(libraryRepository.findAll()).thenReturn(libraries);

      var result = libraryService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(library, library2);
      verify(libraryRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no libraries exist")
    void shouldReturnEmptyList() {
      when(libraryRepository.findAll()).thenReturn(List.of());

      var result = libraryService.findAll();

      assertThat(result).isEmpty();
      verify(libraryRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return library when found")
    void shouldReturnLibraryWhenFound() {
      when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));

      var result = libraryService.findById(libraryId);

      assertThat(result)
          .isPresent()
          .contains(library);
      verify(libraryRepository).findById(libraryId);
    }

    @Test
    @DisplayName("should return empty optional when library not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());

      var result = libraryService.findById(libraryId);

      assertThat(result).isEmpty();
      verify(libraryRepository).findById(libraryId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return library")
    void shouldSaveAndReturnLibrary() {
      when(libraryRepository.save(library)).thenReturn(library);

      var result = libraryService.save(library);

      assertThat(result)
          .isNotNull()
          .isEqualTo(library);
      verify(libraryRepository).save(library);
    }

    @Test
    @DisplayName("should save new library and assign id")
    void shouldSaveNewLibraryAndAssignId() {
      var newLibrary = Library.builder()
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();
      var savedLibrary = Library.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      when(libraryRepository.save(newLibrary)).thenReturn(savedLibrary);

      var result = libraryService.save(newLibrary);

      assertThat(result.getId()).isNotNull();
      verify(libraryRepository).save(newLibrary);
    }

    @Test
    @DisplayName("should update existing library")
    void shouldUpdateExistingLibrary() {
      var updatedLibrary = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.COMPLETED)
          .build();

      when(libraryRepository.save(updatedLibrary)).thenReturn(updatedLibrary);

      var result = libraryService.save(updatedLibrary);

      assertThat(result.getStatus()).isEqualTo(LibraryStatus.COMPLETED);
      verify(libraryRepository).save(updatedLibrary);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when library exists")
    void shouldReturnTrueWhenLibraryExists() {
      when(libraryRepository.existsById(libraryId)).thenReturn(true);

      var result = libraryService.existsById(libraryId);

      assertThat(result).isTrue();
      verify(libraryRepository).existsById(libraryId);
    }

    @Test
    @DisplayName("should return false when library does not exist")
    void shouldReturnFalseWhenLibraryDoesNotExist() {
      when(libraryRepository.existsById(libraryId)).thenReturn(false);

      var result = libraryService.existsById(libraryId);

      assertThat(result).isFalse();
      verify(libraryRepository).existsById(libraryId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete library by id")
    void shouldDeleteLibraryById() {
      libraryService.deleteById(libraryId);

      verify(libraryRepository).deleteById(libraryId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent library")
    void shouldHandleDeletionOfNonExistentLibrary() {
      var nonExistentId = UUID.randomUUID();

      libraryService.deleteById(nonExistentId);

      verify(libraryRepository).deleteById(nonExistentId);
    }
  }

  @Nested
  @DisplayName("findContinueReading()")
  class FindContinueReadingTests {

    @Test
    @DisplayName("should return continue reading list with all fields")
    void shouldReturnContinueReadingListWithAllFields() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result)
          .isNotNull()
          .hasSize(1);
      var item = result.getFirst();
      assertThat(item.workId()).isEqualTo(workId);
      assertThat(item.slug()).isEqualTo("test-slug");
      assertThat(item.libraryStatus()).isEqualTo("READING");
      assertThat(item.chapterCount()).isEqualTo(10L);
      assertThat(item.unreadCount()).isEqualTo(5L);
      assertThat(item.coverUrl()).isEqualTo("https://storage.example.com/bucket/shounen/test-slug/covers/cover.jpg");
      assertThat(item.originalLanguageCode()).isEqualTo("ja");
      assertThat(item.originalLanguageFlag()).isEqualTo("🇯🇵");
      assertThat(item.originalLanguageName()).isEqualTo("Japanese");
      verify(libraryRepository).findContinueReadingByUserId(userId, 6);
    }

    @Test
    @DisplayName("should handle null cover file name")
    void shouldHandleNullCoverFileName() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", null, 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle null slug")
    void shouldHandleNullSlug() {
      Object[] row = new Object[]{
          libraryId, workId, null, "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().coverUrl()).isNull();
    }

    @Test
    @DisplayName("should use unknown demographic when null")
    void shouldUseUnknownDemographicWhenNull() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", null, "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().coverUrl()).isEqualTo("https://storage.example.com/bucket/unknown/test-slug/covers/cover.jpg");
    }

    @Test
    @DisplayName("should handle null work id")
    void shouldHandleNullWorkId() {
      Object[] row = new Object[]{
          libraryId, null, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).hasSize(1);
      assertThat(result.getFirst().workId()).isNull();
    }

    @Test
    @DisplayName("should handle null number fields")
    void shouldHandleNullNumberFields() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          null, "Test Title", "cover.jpg", null, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);

      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(rows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).hasSize(1);
      var item = result.getFirst();
      assertThat(item.chapterCount()).isEqualTo(0L);
      assertThat(item.unreadCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should return empty list when no results")
    void shouldReturnEmptyListWhenNoResults() {
      List<Object[]> emptyRows = new ArrayList<>();
      when(libraryRepository.findContinueReadingByUserId(userId, 6)).thenReturn(emptyRows);

      var result = libraryService.findContinueReading(userId, 6, storageBaseUrl);

      assertThat(result).isEmpty();
      verify(libraryRepository).findContinueReadingByUserId(userId, 6);
    }

    @Test
    @DisplayName("should respect limit parameter")
    void shouldRespectLimitParameter() {
      when(libraryRepository.findContinueReadingByUserId(userId, 10)).thenReturn(List.of());

      libraryService.findContinueReading(userId, 10, storageBaseUrl);

      verify(libraryRepository).findContinueReadingByUserId(userId, 10);
    }
  }

  @Nested
  @DisplayName("findStatusMapByUserAndWorkIds()")
  class FindStatusMapByUserAndWorkIdsTests {

    @Test
    @DisplayName("should return status map for given work ids")
    void shouldReturnStatusMapForGivenWorkIds() {
      var work2Id = UUID.randomUUID();
      var workIds = List.of(workId, work2Id);
      List<Object[]> rows = new ArrayList<>();
      rows.add(new Object[]{workId, LibraryStatus.READING});
      rows.add(new Object[]{work2Id, LibraryStatus.COMPLETED});

      when(libraryRepository.findWorkIdAndStatusByUserIdAndWorkIdIn(userId, workIds)).thenReturn(rows);

      var result = libraryService.findStatusMapByUserAndWorkIds(user, workIds);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .containsEntry(workId, LibraryStatus.READING)
          .containsEntry(work2Id, LibraryStatus.COMPLETED);
      verify(libraryRepository).findWorkIdAndStatusByUserIdAndWorkIdIn(userId, workIds);
    }

    @Test
    @DisplayName("should return empty map for null work ids")
    void shouldReturnEmptyMapForNullWorkIds() {
      var result = libraryService.findStatusMapByUserAndWorkIds(user, null);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return empty map for empty work ids")
    void shouldReturnEmptyMapForEmptyWorkIds() {
      var result = libraryService.findStatusMapByUserAndWorkIds(user, List.of());

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return single entry map")
    void shouldReturnSingleEntryMap() {
      var workIds = List.of(workId);

      List<Object[]> rows = new ArrayList<>();
      rows.add(new Object[]{workId, LibraryStatus.READING});
      when(libraryRepository.findWorkIdAndStatusByUserIdAndWorkIdIn(userId, workIds)).thenReturn(rows);

      var result = libraryService.findStatusMapByUserAndWorkIds(user, workIds);

      assertThat(result)
          .hasSize(1)
          .containsEntry(workId, LibraryStatus.READING);
    }

    @Test
    @DisplayName("should handle multiple statuses")
    void shouldHandleMultipleStatuses() {
      var work2Id = UUID.randomUUID();
      var work3Id = UUID.randomUUID();
      var workIds = List.of(workId, work2Id, work3Id);
      List<Object[]> rows = new ArrayList<>();
      rows.add(new Object[]{workId, LibraryStatus.READING});
      rows.add(new Object[]{work2Id, LibraryStatus.COMPLETED});
      rows.add(new Object[]{work3Id, LibraryStatus.PLAN_TO_READ});

      when(libraryRepository.findWorkIdAndStatusByUserIdAndWorkIdIn(userId, workIds)).thenReturn(rows);

      var result = libraryService.findStatusMapByUserAndWorkIds(user, workIds);

      assertThat(result)
          .hasSize(3)
          .containsEntry(workId, LibraryStatus.READING)
          .containsEntry(work2Id, LibraryStatus.COMPLETED)
          .containsEntry(work3Id, LibraryStatus.PLAN_TO_READ);
    }
  }

  @Nested
  @DisplayName("findByUserAndWork()")
  class FindByUserAndWorkTests {

    @Test
    @DisplayName("should return library when found")
    void shouldReturnLibraryWhenFound() {
      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.of(library));

      var result = libraryService.findByUserAndWork(user, work);

      assertThat(result)
          .isPresent()
          .contains(library);
      verify(libraryRepository).findByUser_IdAndWork_Id(userId, workId);
    }

    @Test
    @DisplayName("should return empty optional when library not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.empty());

      var result = libraryService.findByUserAndWork(user, work);

      assertThat(result).isEmpty();
      verify(libraryRepository).findByUser_IdAndWork_Id(userId, workId);
    }
  }

  @Nested
  @DisplayName("saveOrUpdate()")
  class SaveOrUpdateTests {

    @Test
    @DisplayName("should save new library entry")
    void shouldSaveNewLibraryEntry() {
      var newLibrary = Library.builder()
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.empty());
      when(libraryRepository.save(any(Library.class))).thenReturn(library);

      var result = libraryService.saveOrUpdate(user, work, LibraryStatus.READING);

      assertThat(result).isNotNull();
      assertThat(result.getStatus()).isEqualTo(LibraryStatus.READING);
      verify(libraryRepository).findByUser_IdAndWork_Id(userId, workId);
      verify(libraryRepository).save(any(Library.class));
    }

    @Test
    @DisplayName("should update existing library entry")
    void shouldUpdateExistingLibraryEntry() {
      var existingLibrary = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.of(existingLibrary));
      when(libraryRepository.save(any(Library.class))).thenReturn(existingLibrary);

      var result = libraryService.saveOrUpdate(user, work, LibraryStatus.COMPLETED);

      assertThat(result).isNotNull();
      verify(libraryRepository).findByUser_IdAndWork_Id(userId, workId);
      verify(libraryRepository).save(any(Library.class));
    }

    @Test
    @DisplayName("should update status to PLAN_TO_READ")
    void shouldUpdateStatusToPlanToRead() {
      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.of(library));
      when(libraryRepository.save(any(Library.class))).thenReturn(library);

      libraryService.saveOrUpdate(user, work, LibraryStatus.PLAN_TO_READ);

      verify(libraryRepository).save(any(Library.class));
    }

    @Test
    @DisplayName("should update status to DROPPED")
    void shouldUpdateStatusToDropped() {
      when(libraryRepository.findByUser_IdAndWork_Id(userId, workId)).thenReturn(Optional.of(library));
      when(libraryRepository.save(any(Library.class))).thenReturn(library);

      libraryService.saveOrUpdate(user, work, LibraryStatus.DROPPED);

      verify(libraryRepository).save(any(Library.class));
    }
  }

  @Nested
  @DisplayName("deleteByUserAndWork()")
  class DeleteByUserAndWorkTests {

    @Test
    @DisplayName("should delete library by user and work")
    void shouldDeleteLibraryByUserAndWork() {
      libraryService.deleteByUserAndWork(user, work);

      verify(libraryRepository).deleteByUserIdAndWorkId(userId, workId);
    }

    @Test
    @DisplayName("should handle deletion when library does not exist")
    void shouldHandleDeletionWhenLibraryDoesNotExist() {
      var nonExistentWork = Work.builder().id(UUID.randomUUID()).build();

      libraryService.deleteByUserAndWork(user, nonExistentWork);

      verify(libraryRepository).deleteByUserIdAndWorkId(userId, nonExistentWork.getId());
    }
  }

  @Nested
  @DisplayName("findUserLibrary()")
  class FindUserLibraryTests {

    @Test
    @DisplayName("should return paginated library items")
    void shouldReturnPaginatedLibraryItems() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      assertThat(result)
          .isNotNull()
          .hasSize(1);
      assertThat(result.getContent().getFirst().slug()).isEqualTo("test-slug");
      verify(libraryRepository).findLibraryItemsByUserId(userId, null, null, pageable);
    }

    @Test
    @DisplayName("should filter by status")
    void shouldFilterByStatus() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, "READING", null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, LibraryStatus.READING, null, pageable, storageBaseUrl);

      assertThat(result).hasSize(1);
      verify(libraryRepository).findLibraryItemsByUserId(userId, "READING", null, pageable);
    }

    @Test
    @DisplayName("should filter by title")
    void shouldFilterByTitle() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, "Test", pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, "Test", pageable, storageBaseUrl);

      assertThat(result).hasSize(1);
      verify(libraryRepository).findLibraryItemsByUserId(userId, null, "Test", pageable);
    }

    @Test
    @DisplayName("should trim whitespace in title filter")
    void shouldTrimWhitespaceInTitleFilter() {
      var pageable = PageRequest.of(0, 20);
      List<Object[]> emptyRows = new ArrayList<>();
      Page<Object[]> page = new PageImpl<>(emptyRows, pageable, 0);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, "Test", pageable)).thenReturn(page);

      libraryService.findUserLibrary(userId, null, "  Test  ", pageable, storageBaseUrl);

      verify(libraryRepository).findLibraryItemsByUserId(userId, null, "Test", pageable);
    }

    @Test
    @DisplayName("should handle null title filter")
    void shouldHandleNullTitleFilter() {
      var pageable = PageRequest.of(0, 20);
      List<Object[]> emptyRows = new ArrayList<>();
      Page<Object[]> page = new PageImpl<>(emptyRows, pageable, 0);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      verify(libraryRepository).findLibraryItemsByUserId(userId, null, null, pageable);
    }

    @Test
    @DisplayName("should handle blank title filter as null")
    void shouldHandleBlankTitleFilterAsNull() {
      var pageable = PageRequest.of(0, 20);
      List<Object[]> emptyRows = new ArrayList<>();
      Page<Object[]> page = new PageImpl<>(emptyRows, pageable, 0);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      libraryService.findUserLibrary(userId, null, "   ", pageable, storageBaseUrl);

      verify(libraryRepository).findLibraryItemsByUserId(userId, null, null, pageable);
    }

    @Test
    @DisplayName("should filter by status and title")
    void shouldFilterByStatusAndTitle() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "COMPLETED", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 0L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, "COMPLETED", "Test", pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, LibraryStatus.COMPLETED, "Test", pageable, storageBaseUrl);

      assertThat(result).hasSize(1);
      verify(libraryRepository).findLibraryItemsByUserId(userId, "COMPLETED", "Test", pageable);
    }

    @Test
    @DisplayName("should return empty page when no results")
    void shouldReturnEmptyPageWhenNoResults() {
      var pageable = PageRequest.of(0, 20);
      List<Object[]> emptyRows = new ArrayList<>();
      Page<Object[]> page = new PageImpl<>(emptyRows, pageable, 0);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should construct correct cover url")
    void shouldConstructCorrectCoverUrl() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      assertThat(result.getContent().getFirst().coverUrl())
          .isEqualTo("https://storage.example.com/bucket/shounen/test-slug/covers/cover.jpg");
    }

    @Test
    @DisplayName("should handle null cover file name")
    void shouldHandleNullCoverFileName() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN", "ONGOING",
          10L, "Test Title", null, 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      assertThat(result.getContent().getFirst().coverUrl()).isNull();
    }

    @Test
    @DisplayName("should handle case insensitive demographic in url")
    void shouldHandleCaseInsensitiveDemographicInUrl() {
      Object[] row = new Object[]{
          libraryId, workId, "test-slug", "READING", "SHOUNEN_AI", "ONGOING",
          10L, "Test Title", "cover.jpg", 5L, "ja", "🇯🇵", "Japanese"
      };
      List<Object[]> rows = new ArrayList<>();
      rows.add(row);
      var pageable = PageRequest.of(0, 20);
      Page<Object[]> page = new PageImpl<>(rows, pageable, 1);

      when(libraryRepository.findLibraryItemsByUserId(userId, null, null, pageable)).thenReturn(page);

      var result = libraryService.findUserLibrary(userId, null, null, pageable, storageBaseUrl);

      assertThat(result.getContent().getFirst().coverUrl())
          .isEqualTo("https://storage.example.com/bucket/shounen_ai/test-slug/covers/cover.jpg");
    }
  }
}
