package dev.williancorrea.manhwa.reader.features.language;

import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LanguageService")
class LanguageServiceTest {

  @Mock
  private LanguageRepository languageRepository;

  @InjectMocks
  private LanguageService languageService;

  private UUID languageId;
  private Language language;

  @BeforeEach
  void setUp() {
    languageId = UUID.randomUUID();
    language = Language.builder()
        .id(languageId)
        .code("en")
        .name("English")
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all languages")
    void shouldReturnAllLanguages() {
      var language2 = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();
      var languages = List.of(language, language2);

      when(languageRepository.findAll()).thenReturn(languages);

      var result = languageService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(language, language2);
      verify(languageRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no languages exist")
    void shouldReturnEmptyList() {
      when(languageRepository.findAll()).thenReturn(List.of());

      var result = languageService.findAll();

      assertThat(result).isEmpty();
      verify(languageRepository).findAll();
    }

    @Test
    @DisplayName("should return all language codes")
    void shouldReturnAllLanguageCodes() {
      var english = Language.builder()
          .id(UUID.randomUUID())
          .code("en")
          .name("English")
          .build();
      var japanese = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();
      var korean = Language.builder()
          .id(UUID.randomUUID())
          .code("ko")
          .name("Korean")
          .build();
      var languages = List.of(english, japanese, korean);

      when(languageRepository.findAll()).thenReturn(languages);

      var result = languageService.findAll();

      assertThat(result)
          .hasSize(3)
          .extracting(Language::getCode)
          .contains("en", "ja", "ko");
      verify(languageRepository).findAll();
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save language and return saved entity")
    void shouldSaveLanguageAndReturnSavedEntity() {
      when(languageRepository.save(language)).thenReturn(language);

      var result = languageService.save(language);

      assertThat(result)
          .isNotNull()
          .isEqualTo(language);
      verify(languageRepository).save(language);
    }

    @Test
    @DisplayName("should save language with all properties")
    void shouldSaveLanguageWithAllProperties() {
      var languageWithFlag = Language.builder()
          .id(languageId)
          .code("en")
          .name("English")
          .flag("🇺🇸")
          .build();

      when(languageRepository.save(languageWithFlag)).thenReturn(languageWithFlag);

      var result = languageService.save(languageWithFlag);

      assertThat(result)
          .satisfies(l -> {
            assertThat(l.getId()).isEqualTo(languageId);
            assertThat(l.getCode()).isEqualTo("en");
            assertThat(l.getName()).isEqualTo("English");
            assertThat(l.getFlag()).isEqualTo("🇺🇸");
          });
      verify(languageRepository).save(languageWithFlag);
    }

    @Test
    @DisplayName("should save multiple languages")
    void shouldSaveMultipleLanguages() {
      var language2 = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();

      when(languageRepository.save(language)).thenReturn(language);
      when(languageRepository.save(language2)).thenReturn(language2);

      var result1 = languageService.save(language);
      var result2 = languageService.save(language2);

      assertThat(result1).isEqualTo(language);
      assertThat(result2).isEqualTo(language2);
      verify(languageRepository).save(language);
      verify(languageRepository).save(language2);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return language when found by code")
    void shouldReturnLanguageWhenFoundByCode() {
      when(languageRepository.findByCode("en")).thenReturn(Optional.of(language));

      var result = languageService.findById("en");

      assertThat(result)
          .isPresent()
          .contains(language);
      verify(languageRepository).findByCode("en");
    }

    @Test
    @DisplayName("should return empty optional when language not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(languageRepository.findByCode("xx")).thenReturn(Optional.empty());

      var result = languageService.findById("xx");

      assertThat(result).isEmpty();
      verify(languageRepository).findByCode("xx");
    }

    @Test
    @DisplayName("should return correct language by code")
    void shouldReturnCorrectLanguageByCode() {
      var japaneseLanguage = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();

      when(languageRepository.findByCode("ja")).thenReturn(Optional.of(japaneseLanguage));

      var result = languageService.findById("ja");

      assertThat(result)
          .isPresent()
          .get()
          .satisfies(l -> {
            assertThat(l.getCode()).isEqualTo("ja");
            assertThat(l.getName()).isEqualTo("Japanese");
          });
      verify(languageRepository).findByCode("ja");
    }

    @Test
    @DisplayName("should find language by different codes")
    void shouldFindLanguageByDifferentCodes() {
      var english = Language.builder()
          .id(UUID.randomUUID())
          .code("en")
          .name("English")
          .build();
      var japanese = Language.builder()
          .id(UUID.randomUUID())
          .code("ja")
          .name("Japanese")
          .build();

      when(languageRepository.findByCode("en")).thenReturn(Optional.of(english));
      when(languageRepository.findByCode("ja")).thenReturn(Optional.of(japanese));

      var resultEnglish = languageService.findById("en");
      var resultJapanese = languageService.findById("ja");

      assertThat(resultEnglish).isPresent().contains(english);
      assertThat(resultJapanese).isPresent().contains(japanese);
      verify(languageRepository).findByCode("en");
      verify(languageRepository).findByCode("ja");
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when language exists")
    void shouldReturnTrueWhenLanguageExists() {
      when(languageRepository.existsById(languageId)).thenReturn(true);

      var result = languageService.existsById(languageId);

      assertThat(result).isTrue();
      verify(languageRepository).existsById(languageId);
    }

    @Test
    @DisplayName("should return false when language does not exist")
    void shouldReturnFalseWhenLanguageDoesNotExist() {
      when(languageRepository.existsById(languageId)).thenReturn(false);

      var result = languageService.existsById(languageId);

      assertThat(result).isFalse();
      verify(languageRepository).existsById(languageId);
    }

    @Test
    @DisplayName("should check existence for multiple languages")
    void shouldCheckExistenceForMultipleLanguages() {
      var englishId = UUID.randomUUID();
      var japaneseId = UUID.randomUUID();
      var koreanId = UUID.randomUUID();

      when(languageRepository.existsById(englishId)).thenReturn(true);
      when(languageRepository.existsById(japaneseId)).thenReturn(true);
      when(languageRepository.existsById(koreanId)).thenReturn(false);

      assertThat(languageService.existsById(englishId)).isTrue();
      assertThat(languageService.existsById(japaneseId)).isTrue();
      assertThat(languageService.existsById(koreanId)).isFalse();

      verify(languageRepository).existsById(englishId);
      verify(languageRepository).existsById(japaneseId);
      verify(languageRepository).existsById(koreanId);
    }
  }

  @Nested
  @DisplayName("findOrCreate()")
  class FindOrCreateTests {

    @Test
    @DisplayName("should return existing language when found")
    void shouldReturnExistingLanguageWhenFound() {
      when(languageRepository.findByCode("en")).thenReturn(Optional.of(language));

      var result = languageService.findOrCreate("en", SynchronizationOriginType.MEDIOCRESCAN);

      assertThat(result).isEqualTo(language);
      verify(languageRepository).findByCode("en");
    }

    @Test
    @DisplayName("should create and save language when not found")
    void shouldCreateAndSaveLanguageWhenNotFound() {
      var newLanguage = Language.builder()
          .code("pt")
          .name("Added by " + SynchronizationOriginType.MANGADEX.name())
          .build();

      when(languageRepository.findByCode("pt")).thenReturn(Optional.empty());
      when(languageRepository.save(any(Language.class))).thenReturn(newLanguage);

      var result = languageService.findOrCreate("pt", SynchronizationOriginType.MANGADEX);

      assertThat(result)
          .isNotNull()
          .satisfies(l -> {
            assertThat(l.getCode()).isEqualTo("pt");
            assertThat(l.getName()).contains(SynchronizationOriginType.MANGADEX.name());
          });
      verify(languageRepository).findByCode("pt");
      verify(languageRepository).save(any(Language.class));
    }

    @Test
    @DisplayName("should create language with correct origin name")
    void shouldCreateLanguageWithCorrectOriginName() {
      var mangotoons = Language.builder()
          .code("ko")
          .name("Added by " + SynchronizationOriginType.MANGOTOONS.name())
          .build();

      when(languageRepository.findByCode("ko")).thenReturn(Optional.empty());
      when(languageRepository.save(any(Language.class))).thenReturn(mangotoons);

      var result = languageService.findOrCreate("ko", SynchronizationOriginType.MANGOTOONS);

      assertThat(result.getName()).isEqualTo("Added by MANGOTOONS");
      verify(languageRepository).save(any(Language.class));
    }

    @Test
    @DisplayName("should handle different synchronization origins")
    void shouldHandleDifferentSynchronizationOrigins() {
      var mediocre = Language.builder()
          .code("en")
          .name("Added by " + SynchronizationOriginType.MEDIOCRESCAN.name())
          .build();
      var lycantoons = Language.builder()
          .code("ja")
          .name("Added by " + SynchronizationOriginType.LYCANTOONS.name())
          .build();

      when(languageRepository.findByCode("en")).thenReturn(Optional.empty());
      when(languageRepository.findByCode("ja")).thenReturn(Optional.empty());
      when(languageRepository.save(any(Language.class)))
          .thenReturn(mediocre)
          .thenReturn(lycantoons);

      var resultMediacrescan = languageService.findOrCreate("en", SynchronizationOriginType.MEDIOCRESCAN);
      var resultLycantoons = languageService.findOrCreate("ja", SynchronizationOriginType.LYCANTOONS);

      assertThat(resultMediacrescan.getName()).contains("MEDIOCRESCAN");
      assertThat(resultLycantoons.getName()).contains("LYCANTOONS");
    }

    @Test
    @DisplayName("should not create duplicate when language already exists")
    void shouldNotCreateDuplicateWhenLanguageAlreadyExists() {
      when(languageRepository.findByCode("en")).thenReturn(Optional.of(language));

      var result1 = languageService.findOrCreate("en", SynchronizationOriginType.MANGADEX);
      var result2 = languageService.findOrCreate("en", SynchronizationOriginType.MEDIOCRESCAN);

      assertThat(result1).isEqualTo(language);
      assertThat(result2).isEqualTo(language);
      verify(languageRepository, times(2)).findByCode("en");
    }
  }
}
