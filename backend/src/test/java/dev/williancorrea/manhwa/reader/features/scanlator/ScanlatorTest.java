package dev.williancorrea.manhwa.reader.features.scanlator;

import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Scanlator")
class ScanlatorTest {

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create scanlator with all fields")
    void shouldCreateScanlatorWithAllFields() {
      var id = UUID.randomUUID();

      var scanlator = Scanlator.builder()
          .id(id)
          .name("Test Scanlator")
          .website("https://test.com")
          .code("TEST")
          .synchronization(SynchronizationOriginType.MANGADEX)
          .build();

      assertThat(scanlator)
          .isNotNull()
          .extracting("id", "name", "website", "code", "synchronization")
          .containsExactly(id, "Test Scanlator", "https://test.com", "TEST", SynchronizationOriginType.MANGADEX);
    }

    @Test
    @DisplayName("should create scanlator without optional website field")
    void shouldCreateScanlatorWithoutOptionalWebsiteField() {
      var scanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("No Website Scanlator")
          .code("NOWEBSITE")
          .synchronization(SynchronizationOriginType.LYCANTOONS)
          .build();

      assertThat(scanlator.getWebsite()).isNull();
    }

    @Test
    @DisplayName("should create scanlator with null id")
    void shouldCreateScanlatorWithNullId() {
      var scanlator = Scanlator.builder()
          .id(null)
          .name("Auto ID Scanlator")
          .code("AUTOID")
          .synchronization(SynchronizationOriginType.MANGOTOONS)
          .build();

      assertThat(scanlator.getId()).isNull();
    }
  }

  @Nested
  @DisplayName("Constructors")
  class ConstructorTests {

    @Test
    @DisplayName("should create scanlator with no-arg constructor")
    void shouldCreateScanlatorWithNoArgConstructor() {
      var scanlator = new Scanlator();

      assertThat(scanlator).isNotNull();
      assertThat(scanlator.getId()).isNull();
      assertThat(scanlator.getName()).isNull();
      assertThat(scanlator.getCode()).isNull();
    }

    @Test
    @DisplayName("should create scanlator with all-arg constructor")
    void shouldCreateScanlatorWithAllArgConstructor() {
      var id = UUID.randomUUID();

      var scanlator = new Scanlator(
          id,
          "All Args Scanlator",
          "https://allargs.com",
          "ALLARGS",
          SynchronizationOriginType.MEDIOCRESCAN
      );

      assertThat(scanlator)
          .isNotNull()
          .extracting("id", "name", "website", "code", "synchronization")
          .containsExactly(id, "All Args Scanlator", "https://allargs.com", "ALLARGS", SynchronizationOriginType.MEDIOCRESCAN);
    }
  }

  @Nested
  @DisplayName("Getters and Setters")
  class GettersAndSettersTests {

    @Test
    @DisplayName("should get and set id")
    void shouldGetAndSetId() {
      var scanlator = new Scanlator();
      var id = UUID.randomUUID();

      scanlator.setId(id);

      assertThat(scanlator.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should get and set name")
    void shouldGetAndSetName() {
      var scanlator = new Scanlator();

      scanlator.setName("New Name");

      assertThat(scanlator.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("should get and set website")
    void shouldGetAndSetWebsite() {
      var scanlator = new Scanlator();

      scanlator.setWebsite("https://example.com");

      assertThat(scanlator.getWebsite()).isEqualTo("https://example.com");
    }

    @Test
    @DisplayName("should get and set code")
    void shouldGetAndSetCode() {
      var scanlator = new Scanlator();

      scanlator.setCode("CODE");

      assertThat(scanlator.getCode()).isEqualTo("CODE");
    }

    @Test
    @DisplayName("should get and set synchronization")
    void shouldGetAndSetSynchronization() {
      var scanlator = new Scanlator();

      scanlator.setSynchronization(SynchronizationOriginType.MANGADEX);

      assertThat(scanlator.getSynchronization()).isEqualTo(SynchronizationOriginType.MANGADEX);
    }
  }

  @Nested
  @DisplayName("Equality and Hash")
  class EqualityAndHashTests {

    @Test
    @DisplayName("should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreTheSame() {
      var id = UUID.randomUUID();
      var scanlator1 = new Scanlator(id, "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);
      var scanlator2 = new Scanlator(id, "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);

      assertThat(scanlator1).isEqualTo(scanlator2);
    }

    @Test
    @DisplayName("should not be equal when id is different")
    void shouldNotBeEqualWhenIdIsDifferent() {
      var scanlator1 = new Scanlator(UUID.randomUUID(), "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);
      var scanlator2 = new Scanlator(UUID.randomUUID(), "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);

      assertThat(scanlator1).isNotEqualTo(scanlator2);
    }

    @Test
    @DisplayName("should have same hash code when all fields are equal")
    void shouldHaveSameHashCodeWhenAllFieldsAreEqual() {
      var id = UUID.randomUUID();
      var scanlator1 = new Scanlator(id, "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);
      var scanlator2 = new Scanlator(id, "Name", "https://test.com", "CODE", SynchronizationOriginType.MANGADEX);

      assertThat(scanlator1).hasSameHashCodeAs(scanlator2);
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should implement Serializable interface")
    void shouldImplementSerializableInterface() {
      var scanlator = Scanlator.builder()
          .id(UUID.randomUUID())
          .name("Serializable Scanlator")
          .code("SERIAL")
          .synchronization(SynchronizationOriginType.MANGADEX)
          .build();

      assertThat(scanlator).isInstanceOf(java.io.Serializable.class);
    }
  }
}
