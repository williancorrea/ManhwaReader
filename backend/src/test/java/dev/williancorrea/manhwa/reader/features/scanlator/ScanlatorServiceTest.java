package dev.williancorrea.manhwa.reader.features.scanlator;

import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScanlatorService")
class ScanlatorServiceTest {

  @Mock
  private ScanlatorRepository scanlatorRepository;

  @InjectMocks
  private ScanlatorService scanlatorService;

  private Scanlator scanlator;
  private UUID scanlatorId;

  @BeforeEach
  void setUp() {
    scanlatorId = UUID.randomUUID();
    scanlator = Scanlator.builder()
        .id(scanlatorId)
        .name("Test Scanlator")
        .code("TEST")
        .website("https://test.com")
        .synchronization(SynchronizationOriginType.MANGADEX)
        .build();
  }

  @Nested
  @DisplayName("findBySynchronization()")
  class FindBySynchronizationTests {

    @Test
    @DisplayName("should return scanlator when found with MANGADEX")
    void shouldReturnScanlatorWhenFoundWithMangadex() {
      when(scanlatorRepository.findBySynchronization("MANGADEX"))
          .thenReturn(Optional.of(scanlator));

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MANGADEX);

      assertThat(result)
          .isPresent()
          .contains(scanlator);
      verify(scanlatorRepository).findBySynchronization("MANGADEX");
    }

    @Test
    @DisplayName("should return scanlator when found with LYCANTOONS")
    void shouldReturnScanlatorWhenFoundWithLycantoons() {
      var lycantoonScanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("Lycantoons Scanlator")
          .code("LYCAN")
          .website("https://lycantoons.com")
          .synchronization(SynchronizationOriginType.LYCANTOONS)
          .build();

      when(scanlatorRepository.findBySynchronization("LYCANTOONS"))
          .thenReturn(Optional.of(lycantoonScanlator));

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.LYCANTOONS);

      assertThat(result)
          .isPresent()
          .contains(lycantoonScanlator);
      verify(scanlatorRepository).findBySynchronization("LYCANTOONS");
    }

    @Test
    @DisplayName("should return scanlator when found with MANGOTOONS")
    void shouldReturnScanlatorWhenFoundWithMangotoons() {
      var mangotoonsScanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("Mangotoons Scanlator")
          .code("MANGO")
          .website("https://mangotoons.com")
          .synchronization(SynchronizationOriginType.MANGOTOONS)
          .build();

      when(scanlatorRepository.findBySynchronization("MANGOTOONS"))
          .thenReturn(Optional.of(mangotoonsScanlator));

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MANGOTOONS);

      assertThat(result)
          .isPresent()
          .contains(mangotoonsScanlator);
      verify(scanlatorRepository).findBySynchronization("MANGOTOONS");
    }

    @Test
    @DisplayName("should return scanlator when found with MEDIOCRESCAN")
    void shouldReturnScanlatorWhenFoundWithMediocrescan() {
      var mediocrescanScanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("Mediocre Scanlator")
          .code("MEDIOCRE")
          .website("https://mediocrescan.com")
          .synchronization(SynchronizationOriginType.MEDIOCRESCAN)
          .build();

      when(scanlatorRepository.findBySynchronization("MEDIOCRESCAN"))
          .thenReturn(Optional.of(mediocrescanScanlator));

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN);

      assertThat(result)
          .isPresent()
          .contains(mediocrescanScanlator);
      verify(scanlatorRepository).findBySynchronization("MEDIOCRESCAN");
    }

    @Test
    @DisplayName("should return empty optional when scanlator not found")
    void shouldReturnEmptyOptionalWhenScanlatorNotFound() {
      when(scanlatorRepository.findBySynchronization("MANGADEX"))
          .thenReturn(Optional.empty());

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MANGADEX);

      assertThat(result).isEmpty();
      verify(scanlatorRepository).findBySynchronization("MANGADEX");
    }

    @Test
    @DisplayName("should convert enum name to string when calling repository")
    void shouldConvertEnumNameToStringWhenCallingRepository() {
      when(scanlatorRepository.findBySynchronization("LYCANTOONS"))
          .thenReturn(Optional.of(scanlator));

      scanlatorService.findBySynchronization(SynchronizationOriginType.LYCANTOONS);

      verify(scanlatorRepository).findBySynchronization(eq("LYCANTOONS"));
    }

    @Test
    @DisplayName("should call repository exactly once")
    void shouldCallRepositoryExactlyOnce() {
      when(scanlatorRepository.findBySynchronization(anyString()))
          .thenReturn(Optional.of(scanlator));

      scanlatorService.findBySynchronization(SynchronizationOriginType.MANGADEX);

      verify(scanlatorRepository).findBySynchronization(anyString());
    }

    @Test
    @DisplayName("should return empty optional with no side effects when not found")
    void shouldReturnEmptyOptionalWithNoSideEffectsWhenNotFound() {
      when(scanlatorRepository.findBySynchronization("MANGOTOONS"))
          .thenReturn(Optional.empty());

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MANGOTOONS);

      assertThat(result).isEmpty();
      verify(scanlatorRepository).findBySynchronization("MANGOTOONS");
    }

    @Test
    @DisplayName("should return correct scanlator data when found")
    void shouldReturnCorrectScanlatorDataWhenFound() {
      var testScanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("Full Test Scanlator")
          .code("FULLTEST")
          .website("https://fulltest.com")
          .synchronization(SynchronizationOriginType.MANGADEX)
          .build();

      when(scanlatorRepository.findBySynchronization("MANGADEX"))
          .thenReturn(Optional.of(testScanlator));

      var result = scanlatorService.findBySynchronization(SynchronizationOriginType.MANGADEX);

      assertThat(result)
          .isPresent()
          .hasValueSatisfying(s -> {
            assertThat(s.getId()).isEqualTo(testScanlator.getId());
            assertThat(s.getName()).isEqualTo("Full Test Scanlator");
            assertThat(s.getCode()).isEqualTo("FULLTEST");
            assertThat(s.getWebsite()).isEqualTo("https://fulltest.com");
            assertThat(s.getSynchronization()).isEqualTo(SynchronizationOriginType.MANGADEX);
          });
    }
  }
}
