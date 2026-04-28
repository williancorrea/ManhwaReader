package dev.williancorrea.manhwa.reader.features.scanlator;

import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScanlatorRepository")
class ScanlatorRepositoryTest {

  @Mock
  private ScanlatorRepository scanlatorRepository;

  private Scanlator mangadexScanlator;
  private UUID scanlatorId;

  @BeforeEach
  void setUp() {
    scanlatorId = UUID.randomUUID();
    mangadexScanlator = Scanlator.builder()
        .id(scanlatorId)
        .name("MangaDex Scanlator")
        .code("MANGADEX_CODE")
        .website("https://mangadex.org")
        .synchronization(SynchronizationOriginType.MANGADEX)
        .build();
  }

  @Nested
  @DisplayName("findBySynchronization()")
  class FindBySynchronizationTests {

    @Test
    @DisplayName("should find scanlator by synchronization")
    void shouldFindScanlatorBySynchronization() {
      when(scanlatorRepository.findBySynchronization("MANGADEX"))
          .thenReturn(Optional.of(mangadexScanlator));

      var result = scanlatorRepository.findBySynchronization("MANGADEX");

      assertThat(result)
          .isPresent()
          .contains(mangadexScanlator);
      verify(scanlatorRepository).findBySynchronization("MANGADEX");
    }

    @Test
    @DisplayName("should return empty optional when synchronization not found")
    void shouldReturnEmptyOptionalWhenSynchronizationNotFound() {
      when(scanlatorRepository.findBySynchronization("UNKNOWN"))
          .thenReturn(Optional.empty());

      var result = scanlatorRepository.findBySynchronization("UNKNOWN");

      assertThat(result).isEmpty();
      verify(scanlatorRepository).findBySynchronization("UNKNOWN");
    }
  }

  @Nested
  @DisplayName("JpaRepository standard methods")
  class JpaRepositoryTests {

    @Test
    @DisplayName("should find scanlator by id")
    void shouldFindScanlatorById() {
      when(scanlatorRepository.findById(scanlatorId))
          .thenReturn(Optional.of(mangadexScanlator));

      var result = scanlatorRepository.findById(scanlatorId);

      assertThat(result)
          .isPresent()
          .contains(mangadexScanlator);
      verify(scanlatorRepository).findById(scanlatorId);
    }

    @Test
    @DisplayName("should save scanlator")
    void shouldSaveScanlator() {
      when(scanlatorRepository.save(any(Scanlator.class)))
          .thenReturn(mangadexScanlator);

      var result = scanlatorRepository.save(mangadexScanlator);

      assertThat(result).isEqualTo(mangadexScanlator);
      verify(scanlatorRepository).save(mangadexScanlator);
    }

    @Test
    @DisplayName("should find all scanlators")
    void shouldFindAllScanlators() {
      var scanlators = List.of(mangadexScanlator);
      when(scanlatorRepository.findAll())
          .thenReturn(scanlators);

      var result = scanlatorRepository.findAll();

      assertThat(result)
          .hasSize(1)
          .contains(mangadexScanlator);
      verify(scanlatorRepository).findAll();
    }

    @Test
    @DisplayName("should delete scanlator by id")
    void shouldDeleteScanlatorById() {
      scanlatorRepository.deleteById(scanlatorId);

      verify(scanlatorRepository).deleteById(scanlatorId);
    }

    @Test
    @DisplayName("should check if scanlator exists")
    void shouldCheckIfScanlatorExists() {
      when(scanlatorRepository.existsById(scanlatorId))
          .thenReturn(true);

      var result = scanlatorRepository.existsById(scanlatorId);

      assertThat(result).isTrue();
      verify(scanlatorRepository).existsById(scanlatorId);
    }

    @Test
    @DisplayName("should count all scanlators")
    void shouldCountAllScanlators() {
      when(scanlatorRepository.count())
          .thenReturn(1L);

      var result = scanlatorRepository.count();

      assertThat(result).isEqualTo(1L);
      verify(scanlatorRepository).count();
    }
  }
}
