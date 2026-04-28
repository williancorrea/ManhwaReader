package dev.williancorrea.manhwa.reader.features.volume;

import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolumeService")
class VolumeServiceTest {

  @Mock
  private VolumeRepository volumeRepository;

  @InjectMocks
  private VolumeService volumeService;

  private UUID volumeId;
  private UUID workId;
  private Volume volume;
  private Work work;
  private String title;
  private Integer number;

  @BeforeEach
  void setUp() {
    volumeId = UUID.randomUUID();
    workId = UUID.randomUUID();
    title = "Volume 1";
    number = 1;

    work = Work.builder()
        .id(workId)
        .slug("test-work")
        .build();

    volume = Volume.builder()
        .id(volumeId)
        .work(work)
        .number(number)
        .title(title)
        .build();
  }

  @Nested
  @DisplayName("findOrCreate()")
  class FindOrCreateTests {

    @Test
    @DisplayName("should return existing volume when found by work and title")
    void shouldReturnExistingVolumeWhenFoundByWorkAndTitle() {
      when(volumeRepository.findByWorkAndTitle(workId, title))
          .thenReturn(Optional.of(volume));

      var result = volumeService.findOrCreate(work, title, number);

      assertThat(result)
          .isNotNull()
          .isEqualTo(volume);
      assertThat(result.getId()).isEqualTo(volumeId);
      assertThat(result.getTitle()).isEqualTo(title);
      assertThat(result.getNumber()).isEqualTo(number);
      verify(volumeRepository).findByWorkAndTitle(workId, title);
    }

    @Test
    @DisplayName("should create new volume when not found")
    void shouldCreateNewVolumeWhenNotFound() {
      var newVolume = Volume.builder()
          .work(work)
          .number(number)
          .title(title)
          .build();
      var savedVolume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(number)
          .title(title)
          .build();

      when(volumeRepository.findByWorkAndTitle(workId, title))
          .thenReturn(Optional.empty());
      when(volumeRepository.save(any(Volume.class)))
          .thenReturn(savedVolume);

      var result = volumeService.findOrCreate(work, title, number);

      assertThat(result)
          .isNotNull()
          .isEqualTo(savedVolume);
      assertThat(result.getId()).isNotNull();
      verify(volumeRepository).findByWorkAndTitle(workId, title);
      verify(volumeRepository).save(any(Volume.class));
    }

    @Test
    @DisplayName("should throw NullPointerException when work is null")
    void shouldThrowNullPointerExceptionWhenWorkIsNull() {
      assertThatThrownBy(() -> volumeService.findOrCreate(null, title, number))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should throw NullPointerException when title is null")
    void shouldThrowNullPointerExceptionWhenTitleIsNull() {
      assertThatThrownBy(() -> volumeService.findOrCreate(work, null, number))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should throw NullPointerException when title is blank")
    void shouldThrowNullPointerExceptionWhenTitleIsBlank() {
      assertThatThrownBy(() -> volumeService.findOrCreate(work, "   ", number))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should pass title as provided to repository")
    void shouldPassTitleAsProvidedToRepository() {
      var providedTitle = "  Volume 1  ";
      when(volumeRepository.findByWorkAndTitle(workId, providedTitle))
          .thenReturn(Optional.of(volume));

      var result = volumeService.findOrCreate(work, providedTitle, number);

      assertThat(result).isEqualTo(volume);
      verify(volumeRepository).findByWorkAndTitle(workId, providedTitle);
    }

    @Test
    @DisplayName("should handle number parameter as null")
    void shouldHandleNumberParameterAsNull() {
      when(volumeRepository.findByWorkAndTitle(workId, title))
          .thenReturn(Optional.empty());
      when(volumeRepository.save(any(Volume.class)))
          .thenReturn(volume);

      var result = volumeService.findOrCreate(work, title, null);

      assertThat(result).isNotNull();
      verify(volumeRepository).save(any(Volume.class));
    }

    @Test
    @DisplayName("should pass correct number to created volume")
    void shouldPassCorrectNumberToCreatedVolume() {
      var newNumber = 5;
      when(volumeRepository.findByWorkAndTitle(workId, title))
          .thenReturn(Optional.empty());
      when(volumeRepository.save(any(Volume.class)))
          .thenAnswer(invocation -> {
            Volume v = invocation.getArgument(0);
            return Volume.builder()
                .id(UUID.randomUUID())
                .work(v.getWork())
                .number(v.getNumber())
                .title(v.getTitle())
                .build();
          });

      var result = volumeService.findOrCreate(work, title, newNumber);

      assertThat(result.getNumber()).isEqualTo(newNumber);
    }

    @Test
    @DisplayName("should find volume with different work and same title")
    void shouldFindVolumeWithDifferentWorkAndSameTitle() {
      var otherWorkId = UUID.randomUUID();
      var otherWork = Work.builder().id(otherWorkId).slug("other-work").build();

      when(volumeRepository.findByWorkAndTitle(otherWorkId, title))
          .thenReturn(Optional.of(volume));

      var result = volumeService.findOrCreate(otherWork, title, number);

      assertThat(result).isEqualTo(volume);
      verify(volumeRepository).findByWorkAndTitle(otherWorkId, title);
    }

    @Test
    @DisplayName("should create volume with minimal data")
    void shouldCreateVolumeWithMinimalData() {
      var minimalTitle = "Vol";
      when(volumeRepository.findByWorkAndTitle(workId, minimalTitle))
          .thenReturn(Optional.empty());
      when(volumeRepository.save(any(Volume.class)))
          .thenReturn(volume);

      var result = volumeService.findOrCreate(work, minimalTitle, number);

      assertThat(result).isNotNull();
      verify(volumeRepository).save(any(Volume.class));
    }

    @Test
    @DisplayName("should create volume with special characters in title")
    void shouldCreateVolumeWithSpecialCharactersInTitle() {
      var specialTitle = "Volume #1: Special!@#";
      when(volumeRepository.findByWorkAndTitle(workId, specialTitle))
          .thenReturn(Optional.empty());
      when(volumeRepository.save(any(Volume.class)))
          .thenReturn(volume);

      var result = volumeService.findOrCreate(work, specialTitle, number);

      assertThat(result).isNotNull();
      verify(volumeRepository).save(any(Volume.class));
    }

    @Test
    @DisplayName("should preserve volume relationships when finding existing")
    void shouldPreserveVolumeRelationshipsWhenFindingExisting() {
      when(volumeRepository.findByWorkAndTitle(workId, title))
          .thenReturn(Optional.of(volume));

      var result = volumeService.findOrCreate(work, title, number);

      assertThat(result.getWork()).isEqualTo(work);
      assertThat(result.getWork().getId()).isEqualTo(workId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return volume")
    void shouldSaveAndReturnVolume() {
      when(volumeRepository.save(volume)).thenReturn(volume);

      var result = volumeService.save(volume);

      assertThat(result)
          .isNotNull()
          .isEqualTo(volume);
      verify(volumeRepository).save(volume);
    }

    @Test
    @DisplayName("should save new volume without id")
    void shouldSaveNewVolumeWithoutId() {
      var newVolume = Volume.builder()
          .work(work)
          .number(number)
          .title(title)
          .build();
      var savedVolume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(number)
          .title(title)
          .build();

      when(volumeRepository.save(newVolume)).thenReturn(savedVolume);

      var result = volumeService.save(newVolume);

      assertThat(result.getId()).isNotNull();
      verify(volumeRepository).save(newVolume);
    }

    @Test
    @DisplayName("should update existing volume")
    void shouldUpdateExistingVolume() {
      var updatedVolume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(2)
          .title("Updated Title")
          .build();

      when(volumeRepository.save(updatedVolume)).thenReturn(updatedVolume);

      var result = volumeService.save(updatedVolume);

      assertThat(result.getNumber()).isEqualTo(2);
      assertThat(result.getTitle()).isEqualTo("Updated Title");
      verify(volumeRepository).save(updatedVolume);
    }

    @Test
    @DisplayName("should save volume with null number")
    void shouldSaveVolumeWithNullNumber() {
      var volumeWithoutNumber = Volume.builder()
          .work(work)
          .number(null)
          .title(title)
          .build();

      when(volumeRepository.save(volumeWithoutNumber)).thenReturn(volumeWithoutNumber);

      var result = volumeService.save(volumeWithoutNumber);

      assertThat(result.getNumber()).isNull();
      verify(volumeRepository).save(volumeWithoutNumber);
    }

    @Test
    @DisplayName("should save multiple volumes sequentially")
    void shouldSaveMultipleVolumesSequentially() {
      var volume2 = Volume.builder()
          .id(UUID.randomUUID())
          .work(work)
          .number(2)
          .title("Volume 2")
          .build();

      when(volumeRepository.save(volume)).thenReturn(volume);
      when(volumeRepository.save(volume2)).thenReturn(volume2);

      var result1 = volumeService.save(volume);
      var result2 = volumeService.save(volume2);

      assertThat(result1).isEqualTo(volume);
      assertThat(result2).isEqualTo(volume2);
      verify(volumeRepository).save(volume);
      verify(volumeRepository).save(volume2);
    }

    @Test
    @DisplayName("should preserve volume data after save")
    void shouldPreserveVolumeDataAfterSave() {
      when(volumeRepository.save(volume)).thenReturn(volume);

      var result = volumeService.save(volume);

      assertThat(result.getId()).isEqualTo(volumeId);
      assertThat(result.getTitle()).isEqualTo(title);
      assertThat(result.getNumber()).isEqualTo(number);
      assertThat(result.getWork()).isEqualTo(work);
    }
  }
}
