package dev.williancorrea.manhwa.reader.features.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileStorageInput")
class FileStorageInputTest {

  @Nested
  @DisplayName("Default Constructor")
  class DefaultConstructor {

    @Test
    @DisplayName("should create empty input with default constructor")
    void shouldCreateEmptyInputWithDefaultConstructor() {
      var input = new FileStorageInput();

      assertNotNull(input);
      assertNull(input.getStoragePath());
      assertNull(input.getMimeType());
      assertNull(input.getSizeBytes());
      assertNull(input.getChecksum());
      assertNull(input.getFileName());
      assertNull(input.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructor {

    @Test
    @DisplayName("should create input with all arguments")
    void shouldCreateInputWithAllArguments() {
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
      var input = new FileStorageInput(
          "path/to/file.jpg",
          "image/jpeg",
          1024L,
          "checksum123",
          "file.jpg",
          createdAt
      );

      assertEquals("path/to/file.jpg", input.getStoragePath());
      assertEquals("image/jpeg", input.getMimeType());
      assertEquals(1024L, input.getSizeBytes());
      assertEquals("checksum123", input.getChecksum());
      assertEquals("file.jpg", input.getFileName());
      assertEquals(createdAt, input.getCreatedAt());
    }

    @Test
    @DisplayName("should create input with nullable fields")
    void shouldCreateInputWithNullableFields() {
      var input = new FileStorageInput("path/file", null, null, null, null, null);

      assertEquals("path/file", input.getStoragePath());
      assertNull(input.getMimeType());
      assertNull(input.getSizeBytes());
      assertNull(input.getChecksum());
      assertNull(input.getFileName());
      assertNull(input.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("Getters and Setters")
  class GettersAndSetters {

    @Test
    @DisplayName("should set and get storagePath")
    void shouldSetAndGetStoragePath() {
      var input = new FileStorageInput();
      input.setStoragePath("path/to/file");

      assertEquals("path/to/file", input.getStoragePath());
    }

    @Test
    @DisplayName("should set and get mimeType")
    void shouldSetAndGetMimeType() {
      var input = new FileStorageInput();
      input.setMimeType("image/png");

      assertEquals("image/png", input.getMimeType());
    }

    @Test
    @DisplayName("should set and get sizeBytes")
    void shouldSetAndGetSizeBytes() {
      var input = new FileStorageInput();
      input.setSizeBytes(2048L);

      assertEquals(2048L, input.getSizeBytes());
    }

    @Test
    @DisplayName("should set and get checksum")
    void shouldSetAndGetChecksum() {
      var input = new FileStorageInput();
      input.setChecksum("abc123def456");

      assertEquals("abc123def456", input.getChecksum());
    }

    @Test
    @DisplayName("should set and get fileName")
    void shouldSetAndGetFileName() {
      var input = new FileStorageInput();
      input.setFileName("document.pdf");

      assertEquals("document.pdf", input.getFileName());
    }

    @Test
    @DisplayName("should set and get createdAt")
    void shouldSetAndGetCreatedAt() {
      var input = new FileStorageInput();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
      input.setCreatedAt(createdAt);

      assertEquals(createdAt, input.getCreatedAt());
    }

    @Test
    @DisplayName("should set and get all properties together")
    void shouldSetAndGetAllPropertiesTogether() {
      var input = new FileStorageInput();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      input.setStoragePath("path/to/file.txt");
      input.setMimeType("text/plain");
      input.setSizeBytes(512L);
      input.setChecksum("checksum789");
      input.setFileName("file.txt");
      input.setCreatedAt(createdAt);

      assertEquals("path/to/file.txt", input.getStoragePath());
      assertEquals("text/plain", input.getMimeType());
      assertEquals(512L, input.getSizeBytes());
      assertEquals("checksum789", input.getChecksum());
      assertEquals("file.txt", input.getFileName());
      assertEquals(createdAt, input.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("Validation Annotations")
  class ValidationAnnotations {

    @Test
    @DisplayName("should allow storagePath with maximum length of 512 characters")
    void shouldAllowStoragePathWithMaximumLength() {
      var input = new FileStorageInput();
      var longPath = "a".repeat(512);
      input.setStoragePath(longPath);

      assertEquals(longPath, input.getStoragePath());
    }

    @Test
    @DisplayName("should allow mimeType with maximum length of 100 characters")
    void shouldAllowMimeTypeWithMaximumLength() {
      var input = new FileStorageInput();
      var longMimeType = "a".repeat(100);
      input.setMimeType(longMimeType);

      assertEquals(longMimeType, input.getMimeType());
    }

    @Test
    @DisplayName("should allow checksum with maximum length of 255 characters")
    void shouldAllowChecksumWithMaximumLength() {
      var input = new FileStorageInput();
      var longChecksum = "a".repeat(255);
      input.setChecksum(longChecksum);

      assertEquals(longChecksum, input.getChecksum());
    }
  }
}
