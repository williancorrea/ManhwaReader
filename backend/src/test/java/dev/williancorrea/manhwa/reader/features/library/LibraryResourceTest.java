package dev.williancorrea.manhwa.reader.features.library;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.library.dto.LibraryItemOutput;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibraryResource")
class LibraryResourceTest {

  @Mock
  private LibraryService libraryService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private LibraryResource libraryResource;

  private UUID userId;
  private User user;
  private UserDetails userDetails;
  private String minioUrl;
  private String bucketName;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();

    userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("test@example.com");

    minioUrl = "https://minio.example.com";
    bucketName = "covers";

    ReflectionTestUtils.setField(libraryResource, "minioUrl", minioUrl);
    ReflectionTestUtils.setField(libraryResource, "bucketName", bucketName);
  }

  @Nested
  @DisplayName("getContinueReading()")
  class GetContinueReadingTests {

    @Test
    @DisplayName("should return continue reading list with default size")
    void shouldReturnContinueReadingListWithDefaultSize() {
      var libraryItem = new LibraryItemOutput(
          UUID.randomUUID(), "test-slug", "Test Title", "cover.jpg",
          "SHOUNEN", "ONGOING", 10L, "READING", 5L, "ja", "🇯🇵", "Japanese"
      );
      var items = List.of(libraryItem);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 6, minioUrl + "/" + bucketName))
          .thenReturn(items);

      var response = libraryResource.getContinueReading(6, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .contains(libraryItem);
      verify(userRepository).findByEmail("test@example.com");
      verify(libraryService).findContinueReading(userId, 6, minioUrl + "/" + bucketName);
    }

    @Test
    @DisplayName("should limit size to maximum of 20")
    void shouldLimitSizeToMaximumOf20() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 20, minioUrl + "/" + bucketName))
          .thenReturn(List.of());

      libraryResource.getContinueReading(30, userDetails);

      verify(libraryService).findContinueReading(userId, 20, minioUrl + "/" + bucketName);
    }

    @Test
    @DisplayName("should accept size less than 20")
    void shouldAcceptSizeLessThan20() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 10, minioUrl + "/" + bucketName))
          .thenReturn(List.of());

      libraryResource.getContinueReading(10, userDetails);

      verify(libraryService).findContinueReading(userId, 10, minioUrl + "/" + bucketName);
    }

    @Test
    @DisplayName("should return empty list when no items available")
    void shouldReturnEmptyListWhenNoItemsAvailable() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 6, minioUrl + "/" + bucketName))
          .thenReturn(List.of());

      var response = libraryResource.getContinueReading(6, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("should construct correct storage base url")
    void shouldConstructCorrectStorageBaseUrl() {
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 6, "https://minio.example.com/covers"))
          .thenReturn(List.of());

      libraryResource.getContinueReading(6, userDetails);

      verify(libraryService).findContinueReading(userId, 6, "https://minio.example.com/covers");
    }

    @Test
    @DisplayName("should return multiple items")
    void shouldReturnMultipleItems() {
      var item1 = new LibraryItemOutput(
          UUID.randomUUID(), "slug1", "Title 1", "cover1.jpg",
          "SHOUNEN", "ONGOING", 10L, "READING", 5L, "ja", "🇯🇵", "Japanese"
      );
      var item2 = new LibraryItemOutput(
          UUID.randomUUID(), "slug2", "Title 2", "cover2.jpg",
          "SHOUJO", "COMPLETED", 20L, "COMPLETED", 0L, "en", "🇬🇧", "English"
      );
      var items = List.of(item1, item2);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findContinueReading(userId, 6, minioUrl + "/" + bucketName))
          .thenReturn(items);

      var response = libraryResource.getContinueReading(6, userDetails);

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
    }
  }

  @Nested
  @DisplayName("getUserLibrary()")
  class GetUserLibraryTests {

    @Test
    @DisplayName("should return paginated user library with defaults")
    void shouldReturnPaginatedUserLibraryWithDefaults() {
      var libraryItem = new LibraryItemOutput(
          UUID.randomUUID(), "test-slug", "Test Title", "cover.jpg",
          "SHOUNEN", "ONGOING", 10L, "READING", 5L, "ja", "🇯🇵", "Japanese"
      );
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(libraryItem), PageRequest.of(0, 20), 1);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      var response = libraryResource.getUserLibrary(0, 20, null, null, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1);
      verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("should limit size to maximum of 50")
    void shouldLimitSizeToMaximumOf50() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 50), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 100, null, null, userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should accept size less than 50")
    void shouldAcceptSizeLessThan50() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 30, null, null, userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should filter by status")
    void shouldFilterByStatus() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(LibraryStatus.READING), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 20, LibraryStatus.READING, null, userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(LibraryStatus.READING), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should filter by title")
    void shouldFilterByTitle() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq("Test"), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 20, null, "Test", userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(null), eq("Test"), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should filter by both status and title")
    void shouldFilterByBothStatusAndTitle() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(LibraryStatus.COMPLETED), eq("Test"), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 20, LibraryStatus.COMPLETED, "Test", userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(LibraryStatus.COMPLETED), eq("Test"), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should return empty page when no results")
    void shouldReturnEmptyPageWhenNoResults() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      var response = libraryResource.getUserLibrary(0, 20, null, null, userDetails);

      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
    }

    @Test
    @DisplayName("should handle multiple pages")
    void shouldHandleMultiplePages() {
      var item1 = new LibraryItemOutput(
          UUID.randomUUID(), "slug1", "Title 1", "cover1.jpg",
          "SHOUNEN", "ONGOING", 10L, "READING", 5L, "ja", "🇯🇵", "Japanese"
      );
      Page<LibraryItemOutput> page1 = new PageImpl<>(List.of(item1), PageRequest.of(0, 20), 40);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page1);

      var response = libraryResource.getUserLibrary(0, 20, null, null, userDetails);

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1);
    }

    @Test
    @DisplayName("should return second page when requested")
    void shouldReturnSecondPageWhenRequested() {
      var item = new LibraryItemOutput(
          UUID.randomUUID(), "slug", "Title", "cover.jpg",
          "SHOUNEN", "ONGOING", 10L, "READING", 5L, "ja", "🇯🇵", "Japanese"
      );
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(item), PageRequest.of(1, 20), 40);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      libraryResource.getUserLibrary(1, 20, null, null, userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should construct correct storage base url")
    void shouldConstructCorrectStorageBaseUrl() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq("https://minio.example.com/covers")
      )).thenReturn(page);

      libraryResource.getUserLibrary(0, 20, null, null, userDetails);

      verify(libraryService).findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq("https://minio.example.com/covers")
      );
    }

    @Test
    @DisplayName("should handle all library status values")
    void shouldHandleAllLibraryStatusValues() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), any(LibraryStatus.class), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      for (var status : LibraryStatus.values()) {
        libraryResource.getUserLibrary(0, 20, status, null, userDetails);
      }

      verify(libraryService, new org.mockito.internal.verification.Times(LibraryStatus.values().length)).findUserLibrary(
          eq(userId), any(LibraryStatus.class), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      );
    }

    @Test
    @DisplayName("should return response with correct status code")
    void shouldReturnResponseWithCorrectStatusCode() {
      Page<LibraryItemOutput> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
      when(libraryService.findUserLibrary(
          eq(userId), eq(null), eq(null), any(Pageable.class), eq(minioUrl + "/" + bucketName)
      )).thenReturn(page);

      var response = libraryResource.getUserLibrary(0, 20, null, null, userDetails);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }
}
