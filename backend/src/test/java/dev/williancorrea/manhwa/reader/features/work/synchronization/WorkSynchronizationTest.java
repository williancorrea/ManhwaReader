package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("WorkSynchronization")
class WorkSynchronizationTest {

  private Work testWork;
  private UUID synchronizationId;

  @BeforeEach
  void setUp() {
    synchronizationId = UUID.randomUUID();
    testWork = Work.builder()
        .id(UUID.randomUUID())
        .slug("test-work")
        .build();
  }

  @Nested
  @DisplayName("builder()")
  class BuilderTests {

    @Test
    @DisplayName("should create entity with all fields")
    void shouldCreateEntityWithAllFields() {
      var createdAt = OffsetDateTime.now();
      var updatedAt = OffsetDateTime.now();

      var synchronization = WorkSynchronization.builder()
          .id(synchronizationId)
          .work(testWork)
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("ext-123")
          .externalSlug("ext-slug")
          .createdWorkAt(createdAt)
          .updatedWorkAt(updatedAt)
          .build();

      assertThat(synchronization.getId()).isEqualTo(synchronizationId);
      assertThat(synchronization.getWork()).isEqualTo(testWork);
      assertThat(synchronization.getOrigin()).isEqualTo(SynchronizationOriginType.MANGADEX);
      assertThat(synchronization.getExternalId()).isEqualTo("ext-123");
      assertThat(synchronization.getExternalSlug()).isEqualTo("ext-slug");
      assertThat(synchronization.getCreatedWorkAt()).isEqualTo(createdAt);
      assertThat(synchronization.getUpdatedWorkAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("should create entity with minimal fields")
    void shouldCreateEntityWithMinimalFields() {
      var synchronization = WorkSynchronization.builder()
          .work(testWork)
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("ext-123")
          .build();

      assertThat(synchronization.getId()).isNull();
      assertThat(synchronization.getWork()).isEqualTo(testWork);
      assertThat(synchronization.getOrigin()).isEqualTo(SynchronizationOriginType.MANGADEX);
      assertThat(synchronization.getExternalId()).isEqualTo("ext-123");
      assertThat(synchronization.getExternalSlug()).isNull();
      assertThat(synchronization.getCreatedWorkAt()).isNull();
      assertThat(synchronization.getUpdatedWorkAt()).isNull();
    }
  }

  @Nested
  @DisplayName("no-arg constructor")
  class NoArgConstructorTests {

    @Test
    @DisplayName("should create empty entity")
    void shouldCreateEmptyEntity() {
      var synchronization = new WorkSynchronization();

      assertThat(synchronization.getId()).isNull();
      assertThat(synchronization.getWork()).isNull();
      assertThat(synchronization.getOrigin()).isNull();
      assertThat(synchronization.getExternalId()).isNull();
      assertThat(synchronization.getExternalSlug()).isNull();
      assertThat(synchronization.getCreatedWorkAt()).isNull();
      assertThat(synchronization.getUpdatedWorkAt()).isNull();
    }
  }

  @Nested
  @DisplayName("all-arg constructor")
  class AllArgConstructorTests {

    @Test
    @DisplayName("should create entity with all arguments")
    void shouldCreateEntityWithAllArguments() {
      var createdAt = OffsetDateTime.now();
      var updatedAt = OffsetDateTime.now();

      var synchronization = new WorkSynchronization(
          synchronizationId,
          testWork,
          SynchronizationOriginType.LYCANTOONS,
          "ext-456",
          "ext-slug-2",
          createdAt,
          updatedAt
      );

      assertThat(synchronization.getId()).isEqualTo(synchronizationId);
      assertThat(synchronization.getWork()).isEqualTo(testWork);
      assertThat(synchronization.getOrigin()).isEqualTo(SynchronizationOriginType.LYCANTOONS);
      assertThat(synchronization.getExternalId()).isEqualTo("ext-456");
      assertThat(synchronization.getExternalSlug()).isEqualTo("ext-slug-2");
      assertThat(synchronization.getCreatedWorkAt()).isEqualTo(createdAt);
      assertThat(synchronization.getUpdatedWorkAt()).isEqualTo(updatedAt);
    }
  }

  @Nested
  @DisplayName("setters and getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get id")
    void shouldSetAndGetId() {
      var synchronization = new WorkSynchronization();
      synchronization.setId(synchronizationId);

      assertThat(synchronization.getId()).isEqualTo(synchronizationId);
    }

    @Test
    @DisplayName("should set and get work")
    void shouldSetAndGetWork() {
      var synchronization = new WorkSynchronization();
      synchronization.setWork(testWork);

      assertThat(synchronization.getWork()).isEqualTo(testWork);
    }

    @Test
    @DisplayName("should set and get origin")
    void shouldSetAndGetOrigin() {
      var synchronization = new WorkSynchronization();
      synchronization.setOrigin(SynchronizationOriginType.MANGADEX);

      assertThat(synchronization.getOrigin()).isEqualTo(SynchronizationOriginType.MANGADEX);
    }

    @Test
    @DisplayName("should set and get externalId")
    void shouldSetAndGetExternalId() {
      var synchronization = new WorkSynchronization();
      synchronization.setExternalId("ext-789");

      assertThat(synchronization.getExternalId()).isEqualTo("ext-789");
    }

    @Test
    @DisplayName("should set and get externalSlug")
    void shouldSetAndGetExternalSlug() {
      var synchronization = new WorkSynchronization();
      synchronization.setExternalSlug("slug-ext");

      assertThat(synchronization.getExternalSlug()).isEqualTo("slug-ext");
    }

    @Test
    @DisplayName("should set and get createdWorkAt")
    void shouldSetAndGetCreatedWorkAt() {
      var synchronization = new WorkSynchronization();
      var now = OffsetDateTime.now();
      synchronization.setCreatedWorkAt(now);

      assertThat(synchronization.getCreatedWorkAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("should set and get updatedWorkAt")
    void shouldSetAndGetUpdatedWorkAt() {
      var synchronization = new WorkSynchronization();
      var now = OffsetDateTime.now();
      synchronization.setUpdatedWorkAt(now);

      assertThat(synchronization.getUpdatedWorkAt()).isEqualTo(now);
    }
  }

  @Nested
  @DisplayName("Serializable")
  class SerializableTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var synchronization = WorkSynchronization.builder()
          .id(synchronizationId)
          .work(testWork)
          .origin(SynchronizationOriginType.MANGADEX)
          .externalId("ext-123")
          .build();

      assertThat(synchronization).isInstanceOf(java.io.Serializable.class);
    }
  }
}
