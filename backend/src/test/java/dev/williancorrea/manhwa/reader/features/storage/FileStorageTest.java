package dev.williancorrea.manhwa.reader.features.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileStorage")
class FileStorageTest {

  @Nested
  @DisplayName("Constructor from FileStorageInput")
  class ConstructorFromFileStorageInput {

    @Test
    @DisplayName("should create entity with all fields when input is complete")
    void shouldCreateEntityWithAllFieldsWhenInputIsComplete() {
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
      var input = new FileStorageInput(
          "path/to/file.jpg",
          "image/jpeg",
          1024L,
          "abc123def456",
          "file.jpg",
          createdAt
      );

      var entity = new FileStorage(input);

      assertNull(entity.getId());
      assertEquals("path/to/file.jpg", entity.getStoragePath());
      assertEquals("image/jpeg", entity.getMimeType());
      assertEquals(1024L, entity.getSizeBytes());
      assertEquals("abc123def456", entity.getChecksum());
      assertEquals("file.jpg", entity.getFileName());
      assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("should create entity with nullable fields when not provided")
    void shouldCreateEntityWithNullableFieldsWhenNotProvided() {
      var input = new FileStorageInput("path/to/file", null, null, null, null, null);

      var entity = new FileStorage(input);

      assertNull(entity.getId());
      assertEquals("path/to/file", entity.getStoragePath());
      assertNull(entity.getMimeType());
      assertNull(entity.getSizeBytes());
      assertNull(entity.getChecksum());
      assertNull(entity.getFileName());
      assertNull(entity.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("Builder")
  class BuilderTest {

    @Test
    @DisplayName("should create entity with all fields using builder")
    void shouldCreateEntityWithAllFieldsUsingBuilder() {
      var id = UUID.randomUUID();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      var entity = FileStorage.builder()
          .id(id)
          .fileName("file.jpg")
          .storagePath("path/to/file.jpg")
          .mimeType("image/jpeg")
          .sizeBytes(1024L)
          .checksum("abc123")
          .createdAt(createdAt)
          .build();

      assertEquals(id, entity.getId());
      assertEquals("file.jpg", entity.getFileName());
      assertEquals("path/to/file.jpg", entity.getStoragePath());
      assertEquals("image/jpeg", entity.getMimeType());
      assertEquals(1024L, entity.getSizeBytes());
      assertEquals("abc123", entity.getChecksum());
      assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    @DisplayName("should create empty entity using builder")
    void shouldCreateEmptyEntityUsingBuilder() {
      var entity = FileStorage.builder().build();

      assertNull(entity.getId());
      assertNull(entity.getFileName());
      assertNull(entity.getStoragePath());
      assertNull(entity.getMimeType());
      assertNull(entity.getSizeBytes());
      assertNull(entity.getChecksum());
      assertNull(entity.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("Getters and Setters")
  class GettersAndSetters {

    @Test
    @DisplayName("should set and get all properties")
    void shouldSetAndGetAllProperties() {
      var entity = new FileStorage();
      var id = UUID.randomUUID();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      entity.setId(id);
      entity.setFileName("test.txt");
      entity.setStoragePath("path/test.txt");
      entity.setMimeType("text/plain");
      entity.setSizeBytes(512L);
      entity.setChecksum("checksum123");
      entity.setCreatedAt(createdAt);

      assertEquals(id, entity.getId());
      assertEquals("test.txt", entity.getFileName());
      assertEquals("path/test.txt", entity.getStoragePath());
      assertEquals("text/plain", entity.getMimeType());
      assertEquals(512L, entity.getSizeBytes());
      assertEquals("checksum123", entity.getChecksum());
      assertEquals(createdAt, entity.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("Default Constructor")
  class DefaultConstructor {

    @Test
    @DisplayName("should create empty entity with default constructor")
    void shouldCreateEmptyEntityWithDefaultConstructor() {
      var entity = new FileStorage();

      assertNotNull(entity);
      assertNull(entity.getId());
      assertNull(entity.getFileName());
      assertNull(entity.getStoragePath());
      assertNull(entity.getMimeType());
      assertNull(entity.getSizeBytes());
      assertNull(entity.getChecksum());
      assertNull(entity.getCreatedAt());
    }
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructor {

    @Test
    @DisplayName("should create entity with all arguments")
    void shouldCreateEntityWithAllArguments() {
      var id = UUID.randomUUID();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      var entity = new FileStorage(
          id,
          "file.pdf",
          "path/to/file.pdf",
          "application/pdf",
          2048L,
          "pdf123checksum",
          createdAt
      );

      assertEquals(id, entity.getId());
      assertEquals("file.pdf", entity.getFileName());
      assertEquals("path/to/file.pdf", entity.getStoragePath());
      assertEquals("application/pdf", entity.getMimeType());
      assertEquals(2048L, entity.getSizeBytes());
      assertEquals("pdf123checksum", entity.getChecksum());
      assertEquals(createdAt, entity.getCreatedAt());
    }
  }
}
