package dev.williancorrea.manhwa.reader.features.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileStorageService")
class FileStorageServiceTest {

  @Mock
  private FileStorageRepository repository;

  @InjectMocks
  private FileStorageService service;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(repository);
  }

  private FileStorage createFileStorage(String fileName) {
    var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    return FileStorage.builder()
        .id(UUID.randomUUID())
        .fileName(fileName)
        .storagePath("path/to/" + fileName)
        .mimeType("image/jpeg")
        .sizeBytes(1024L)
        .checksum("checksum123")
        .createdAt(createdAt)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAll {

    @Test
    @DisplayName("should return all file storages when repository has data")
    void shouldReturnAllFileStoragesWhenRepositoryHasData() {
      var file1 = createFileStorage("file1.jpg");
      var file2 = createFileStorage("file2.jpg");
      var expectedList = List.of(file1, file2);

      when(repository.findAll()).thenReturn(expectedList);

      var result = service.findAll();

      assertNotNull(result);
      assertEquals(2, result.size());
      assertSame(expectedList, result);
      verify(repository).findAll();
    }

    @Test
    @DisplayName("should return empty list when repository is empty")
    void shouldReturnEmptyListWhenRepositoryIsEmpty() {
      when(repository.findAll()).thenReturn(List.of());

      var result = service.findAll();

      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(repository).findAll();
    }

    @Test
    @DisplayName("should return unmodifiable list")
    void shouldReturnUnmodifiableList() {
      var file = createFileStorage("file.jpg");
      when(repository.findAll()).thenReturn(List.of(file));

      var result = service.findAll();

      assertEquals(1, result.size());
      verify(repository).findAll();
    }
  }

  @Nested
  @DisplayName("findById(UUID)")
  class FindById {

    @Test
    @DisplayName("should return file storage when id exists")
    void shouldReturnFileStorageWhenIdExists() {
      var id = UUID.randomUUID();
      var expectedFile = createFileStorage("file.jpg");

      when(repository.findById(id)).thenReturn(Optional.of(expectedFile));

      var result = service.findById(id);

      assertTrue(result.isPresent());
      assertSame(expectedFile, result.get());
      verify(repository).findById(id);
    }

    @Test
    @DisplayName("should return empty optional when id does not exist")
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
      var id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      var result = service.findById(id);

      assertTrue(result.isEmpty());
      verify(repository).findById(id);
    }

    @Test
    @DisplayName("should pass correct id to repository")
    void shouldPassCorrectIdToRepository() {
      var id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      service.findById(id);

      verify(repository).findById(id);
    }
  }

  @Nested
  @DisplayName("save(FileStorage)")
  class Save {

    @Test
    @DisplayName("should save and return file storage")
    void shouldSaveAndReturnFileStorage() {
      var fileToSave = FileStorage.builder()
          .fileName("new_file.jpg")
          .storagePath("path/to/new_file.jpg")
          .mimeType("image/jpeg")
          .sizeBytes(2048L)
          .checksum("checksum456")
          .build();

      var savedFile = createFileStorage("new_file.jpg");

      when(repository.save(fileToSave)).thenReturn(savedFile);

      var result = service.save(fileToSave);

      assertSame(savedFile, result);
      verify(repository).save(fileToSave);
    }

    @Test
    @DisplayName("should save file storage with all fields")
    void shouldSaveFileStorageWithAllFields() {
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
      var fileToSave = FileStorage.builder()
          .fileName("test.pdf")
          .storagePath("path/to/test.pdf")
          .mimeType("application/pdf")
          .sizeBytes(4096L)
          .checksum("checksumpdf")
          .createdAt(createdAt)
          .build();

      var savedFile = FileStorage.builder()
          .id(UUID.randomUUID())
          .fileName("test.pdf")
          .storagePath("path/to/test.pdf")
          .mimeType("application/pdf")
          .sizeBytes(4096L)
          .checksum("checksumpdf")
          .createdAt(createdAt)
          .build();

      when(repository.save(fileToSave)).thenReturn(savedFile);

      var result = service.save(fileToSave);

      assertNotNull(result.getId());
      assertEquals("test.pdf", result.getFileName());
      assertEquals("path/to/test.pdf", result.getStoragePath());
      verify(repository).save(fileToSave);
    }

    @Test
    @DisplayName("should handle null file storage gracefully")
    void shouldHandleNullFileStorageGracefully() {
      when(repository.save(null)).thenReturn(null);

      var result = service.save(null);

      assertEquals(null, result);
      verify(repository).save(null);
    }
  }

  @Nested
  @DisplayName("existsById(UUID)")
  class ExistsById {

    @Test
    @DisplayName("should return true when file storage exists")
    void shouldReturnTrueWhenFileStorageExists() {
      var id = UUID.randomUUID();

      when(repository.existsById(id)).thenReturn(true);

      var result = service.existsById(id);

      assertTrue(result);
      verify(repository).existsById(id);
    }

    @Test
    @DisplayName("should return false when file storage does not exist")
    void shouldReturnFalseWhenFileStorageDoesNotExist() {
      var id = UUID.randomUUID();

      when(repository.existsById(id)).thenReturn(false);

      var result = service.existsById(id);

      assertFalse(result);
      verify(repository).existsById(id);
    }

    @Test
    @DisplayName("should pass correct id to repository")
    void shouldPassCorrectIdToRepository() {
      var id = UUID.randomUUID();

      when(repository.existsById(id)).thenReturn(false);

      service.existsById(id);

      verify(repository).existsById(id);
    }
  }

  @Nested
  @DisplayName("deleteById(UUID)")
  class DeleteById {

    @Test
    @DisplayName("should delete file storage by id")
    void shouldDeleteFileStorageById() {
      var id = UUID.randomUUID();

      service.deleteById(id);

      verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("should pass correct id to repository")
    void shouldPassCorrectIdToRepository() {
      var id = UUID.randomUUID();

      service.deleteById(id);

      verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("should handle multiple delete operations")
    void shouldHandleMultipleDeleteOperations() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();

      service.deleteById(id1);
      service.deleteById(id2);

      var inOrder = inOrder(repository);
      inOrder.verify(repository).deleteById(id1);
      inOrder.verify(repository).deleteById(id2);
    }
  }

  @Nested
  @DisplayName("Integration scenarios")
  class IntegrationScenarios {

    @Test
    @DisplayName("should handle save and find workflow")
    void shouldHandleSaveAndFindWorkflow() {
      var id = UUID.randomUUID();
      var fileToSave = FileStorage.builder()
          .fileName("workflow.jpg")
          .storagePath("path/to/workflow.jpg")
          .mimeType("image/jpeg")
          .sizeBytes(3072L)
          .checksum("workflow123")
          .build();

      var savedFile = FileStorage.builder()
          .id(id)
          .fileName("workflow.jpg")
          .storagePath("path/to/workflow.jpg")
          .mimeType("image/jpeg")
          .sizeBytes(3072L)
          .checksum("workflow123")
          .build();

      when(repository.save(fileToSave)).thenReturn(savedFile);
      when(repository.findById(id)).thenReturn(Optional.of(savedFile));

      var saveResult = service.save(fileToSave);
      var findResult = service.findById(saveResult.getId());

      assertTrue(findResult.isPresent());
      assertEquals("workflow.jpg", findResult.get().getFileName());
      verify(repository).save(fileToSave);
      verify(repository).findById(id);
    }

    @Test
    @DisplayName("should handle save and delete workflow")
    void shouldHandleSaveAndDeleteWorkflow() {
      var id = UUID.randomUUID();
      var fileToSave = FileStorage.builder()
          .fileName("temp.jpg")
          .storagePath("path/to/temp.jpg")
          .build();

      var savedFile = FileStorage.builder()
          .id(id)
          .fileName("temp.jpg")
          .storagePath("path/to/temp.jpg")
          .build();

      when(repository.save(fileToSave)).thenReturn(savedFile);
      when(repository.existsById(id)).thenReturn(true);

      var saveResult = service.save(fileToSave);
      var existsBeforeDelete = service.existsById(saveResult.getId());
      service.deleteById(saveResult.getId());

      assertTrue(existsBeforeDelete);
      verify(repository).save(fileToSave);
      verify(repository).existsById(id);
      verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("should handle findAll, check exists, and delete workflow")
    void shouldHandleFindAllCheckExistsAndDeleteWorkflow() {
      var file1 = createFileStorage("file1.jpg");
      var file2 = createFileStorage("file2.jpg");
      var expectedList = List.of(file1, file2);

      when(repository.findAll()).thenReturn(expectedList);
      when(repository.existsById(file1.getId())).thenReturn(true);

      var allFiles = service.findAll();
      var firstFileExists = service.existsById(file1.getId());
      service.deleteById(file1.getId());

      assertEquals(2, allFiles.size());
      assertTrue(firstFileExists);
      verify(repository).findAll();
      verify(repository).existsById(file1.getId());
      verify(repository).deleteById(file1.getId());
    }
  }
}
