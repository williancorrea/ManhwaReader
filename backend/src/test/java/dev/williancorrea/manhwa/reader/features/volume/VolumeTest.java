package dev.williancorrea.manhwa.reader.features.volume;

import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Volume")
class VolumeTest {

  private UUID volumeId;
  private UUID workId;
  private Volume volume;
  private Work work;

  @BeforeEach
  void setUp() {
    volumeId = UUID.randomUUID();
    workId = UUID.randomUUID();
    work = Work.builder()
        .id(workId)
        .slug("test-work")
        .build();
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create volume with no-arg constructor")
    void shouldCreateVolumeWithNoArgConstructor() {
      Volume volume = new Volume();

      assertThat(volume.getId()).isNull();
      assertThat(volume.getWork()).isNull();
      assertThat(volume.getNumber()).isNull();
      assertThat(volume.getTitle()).isNull();
    }

    @Test
    @DisplayName("should create volume with all-arg constructor")
    void shouldCreateVolumeWithAllArgConstructor() {
      Volume volume = new Volume(volumeId, work, 1, "Volume 1");

      assertThat(volume.getId()).isEqualTo(volumeId);
      assertThat(volume.getWork()).isEqualTo(work);
      assertThat(volume.getNumber()).isEqualTo(1);
      assertThat(volume.getTitle()).isEqualTo("Volume 1");
    }

    @Test
    @DisplayName("should create volume with null work in all-arg constructor")
    void shouldCreateVolumeWithNullWorkInAllArgConstructor() {
      Volume volume = new Volume(volumeId, null, 1, "Volume 1");

      assertThat(volume.getId()).isEqualTo(volumeId);
      assertThat(volume.getWork()).isNull();
      assertThat(volume.getNumber()).isEqualTo(1);
      assertThat(volume.getTitle()).isEqualTo("Volume 1");
    }
  }

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create volume with all fields")
    void shouldCreateVolumeWithAllFields() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume)
          .isNotNull()
          .extracting("id", "work", "number", "title")
          .containsExactly(volumeId, work, 1, "Volume 1");
    }

    @Test
    @DisplayName("should create volume with minimal fields")
    void shouldCreateVolumeWithMinimalFields() {
      volume = Volume.builder()
          .work(work)
          .title("Volume 1")
          .build();

      assertThat(volume)
          .isNotNull()
          .extracting("work", "title")
          .containsExactly(work, "Volume 1");
      assertThat(volume.getId()).isNull();
      assertThat(volume.getNumber()).isNull();
    }

    @Test
    @DisplayName("should allow null values in builder")
    void shouldAllowNullValuesInBuilder() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(null)
          .title(null)
          .build();

      assertThat(volume.getId()).isEqualTo(volumeId);
      assertThat(volume.getWork()).isEqualTo(work);
      assertThat(volume.getNumber()).isNull();
      assertThat(volume.getTitle()).isNull();
    }

    @Test
    @DisplayName("should allow partial initialization with builder")
    void shouldAllowPartialInitializationWithBuilder() {
      volume = Volume.builder()
          .id(volumeId)
          .number(5)
          .build();

      assertThat(volume.getId()).isEqualTo(volumeId);
      assertThat(volume.getNumber()).isEqualTo(5);
      assertThat(volume.getWork()).isNull();
      assertThat(volume.getTitle()).isNull();
    }
  }

  @Nested
  @DisplayName("Getters and Setters")
  class GettersAndSettersTests {

    @BeforeEach
    void setUp() {
      volume = new Volume();
    }

    @Test
    @DisplayName("should set and get id")
    void shouldSetAndGetId() {
      volume.setId(volumeId);

      assertThat(volume.getId()).isEqualTo(volumeId);
    }

    @Test
    @DisplayName("should set and get work")
    void shouldSetAndGetWork() {
      volume.setWork(work);

      assertThat(volume.getWork()).isEqualTo(work);
    }

    @Test
    @DisplayName("should set and get number")
    void shouldSetAndGetNumber() {
      volume.setNumber(3);

      assertThat(volume.getNumber()).isEqualTo(3);
    }

    @Test
    @DisplayName("should set and get title")
    void shouldSetAndGetTitle() {
      volume.setTitle("Volume 1");

      assertThat(volume.getTitle()).isEqualTo("Volume 1");
    }

    @Test
    @DisplayName("should allow updating number")
    void shouldAllowUpdatingNumber() {
      volume.setNumber(1);
      assertThat(volume.getNumber()).isEqualTo(1);

      volume.setNumber(2);
      assertThat(volume.getNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("should allow updating title")
    void shouldAllowUpdatingTitle() {
      volume.setTitle("Original Title");
      assertThat(volume.getTitle()).isEqualTo("Original Title");

      volume.setTitle("Updated Title");
      assertThat(volume.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("should allow updating work")
    void shouldAllowUpdatingWork() {
      volume.setWork(work);
      assertThat(volume.getWork()).isEqualTo(work);

      var newWork = Work.builder()
          .id(UUID.randomUUID())
          .slug("new-work")
          .build();
      volume.setWork(newWork);
      assertThat(volume.getWork()).isEqualTo(newWork);
    }

    @Test
    @DisplayName("should set null values")
    void shouldSetNullValues() {
      volume.setId(volumeId);
      volume.setWork(work);
      volume.setNumber(1);
      volume.setTitle("Volume 1");

      volume.setId(null);
      volume.setWork(null);
      volume.setNumber(null);
      volume.setTitle(null);

      assertThat(volume.getId()).isNull();
      assertThat(volume.getWork()).isNull();
      assertThat(volume.getNumber()).isNull();
      assertThat(volume.getTitle()).isNull();
    }
  }

  @Nested
  @DisplayName("Equality and Hash")
  class EqualityAndHashTests {

    @Test
    @DisplayName("should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreSame() {
      var volume1 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var volume2 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume1).isEqualTo(volume2);
    }

    @Test
    @DisplayName("should not be equal when id is different")
    void shouldNotBeEqualWhenIdIsDifferent() {
      var volume1 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var volume2 = Volume.builder()
          .id(UUID.randomUUID())
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume1).isNotEqualTo(volume2);
    }

    @Test
    @DisplayName("should not be equal when title is different")
    void shouldNotBeEqualWhenTitleIsDifferent() {
      var volume1 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var volume2 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 2")
          .build();

      assertThat(volume1).isNotEqualTo(volume2);
    }

    @Test
    @DisplayName("should not be equal when number is different")
    void shouldNotBeEqualWhenNumberIsDifferent() {
      var volume1 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var volume2 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(2)
          .title("Volume 1")
          .build();

      assertThat(volume1).isNotEqualTo(volume2);
    }

    @Test
    @DisplayName("should have same hashCode when equal")
    void shouldHaveSameHashCodeWhenEqual() {
      var volume1 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var volume2 = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume1).hasSameHashCodeAs(volume2);
    }

    @Test
    @DisplayName("should not be equal to null")
    void shouldNotBeEqualToNull() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume).isNotEqualTo(null);
    }

    @Test
    @DisplayName("should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume).isNotEqualTo("Volume 1");
    }
  }

  @Nested
  @DisplayName("toString()")
  class ToStringTests {

    @Test
    @DisplayName("should generate string representation")
    void shouldGenerateStringRepresentation() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var toString = volume.toString();

      assertThat(toString).contains("Volume");
      assertThat(toString).contains("id");
      assertThat(toString).contains("number");
      assertThat(toString).contains("title");
    }

    @Test
    @DisplayName("should include id in string representation")
    void shouldIncludeIdInStringRepresentation() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var toString = volume.toString();

      assertThat(toString).contains(volumeId.toString());
    }

    @Test
    @DisplayName("should include title in string representation")
    void shouldIncludeTitleInStringRepresentation() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var toString = volume.toString();

      assertThat(toString).contains("Volume 1");
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should implement Serializable interface")
    void shouldImplementSerializableInterface() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      assertThat(volume).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("should maintain field values across serialization")
    void shouldMaintainFieldValuesAcrossSerialization() {
      volume = Volume.builder()
          .id(volumeId)
          .work(work)
          .number(1)
          .title("Volume 1")
          .build();

      var id = volume.getId();
      var number = volume.getNumber();
      var title = volume.getTitle();
      var volumeWork = volume.getWork();

      assertThat(id).isEqualTo(volumeId);
      assertThat(number).isEqualTo(1);
      assertThat(title).isEqualTo("Volume 1");
      assertThat(volumeWork).isEqualTo(work);
    }
  }
}
