package dev.williancorrea.manhwa.reader.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SystemConfiguration")
class SystemConfigurationTest {

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create system configuration with all fields")
    void shouldCreateSystemConfigurationWithAllFields() {
      var id = UUID.randomUUID();
      var groupId = UUID.randomUUID();
      var group = SystemConfigurationGroup.builder()
          .id(groupId)
          .name("Test Group")
          .active(true)
          .build();

      var config = SystemConfiguration.builder()
          .id(id)
          .group(group)
          .description("Test Description")
          .reference("TEST_REF")
          .value("test_value")
          .active(true)
          .build();

      assertThat(config).isNotNull();
      assertThat(config.getId()).isEqualTo(id);
      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getDescription()).isEqualTo("Test Description");
      assertThat(config.getReference()).isEqualTo("TEST_REF");
      assertThat(config.getValue()).isEqualTo("test_value");
      assertThat(config.isActive()).isTrue();
    }

    @Test
    @DisplayName("should create system configuration with minimum required fields")
    void shouldCreateSystemConfigurationWithMinimumFields() {
      var id = UUID.randomUUID();
      var group = new SystemConfigurationGroup();
      group.setId(UUID.randomUUID());

      var config = SystemConfiguration.builder()
          .id(id)
          .group(group)
          .reference("MIN_REF")
          .value("min_value")
          .active(false)
          .build();

      assertThat(config).isNotNull();
      assertThat(config.getId()).isEqualTo(id);
      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getDescription()).isNull();
      assertThat(config.getReference()).isEqualTo("MIN_REF");
      assertThat(config.getValue()).isEqualTo("min_value");
      assertThat(config.isActive()).isFalse();
    }

    @Test
    @DisplayName("should create system configuration without id")
    void shouldCreateSystemConfigurationWithoutId() {
      var group = new SystemConfigurationGroup();

      var config = SystemConfiguration.builder()
          .group(group)
          .description("No ID Description")
          .reference("NO_ID_REF")
          .value("no_id_value")
          .active(true)
          .build();

      assertThat(config).isNotNull();
      assertThat(config.getId()).isNull();
      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getDescription()).isEqualTo("No ID Description");
    }
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create system configuration with no-args constructor")
    void shouldCreateSystemConfigurationWithNoArgsConstructor() {
      var config = new SystemConfiguration();

      assertThat(config).isNotNull();
      assertThat(config.getId()).isNull();
      assertThat(config.getGroup()).isNull();
      assertThat(config.getDescription()).isNull();
      assertThat(config.getReference()).isNull();
      assertThat(config.getValue()).isNull();
      assertThat(config.isActive()).isFalse();
    }

    @Test
    @DisplayName("should create system configuration with all-args constructor")
    void shouldCreateSystemConfigurationWithAllArgsConstructor() {
      var id = UUID.randomUUID();
      var group = new SystemConfigurationGroup();
      group.setId(UUID.randomUUID());
      group.setName("Test Group");
      group.setActive(true);

      var config = new SystemConfiguration(
          id,
          group,
          "Full Constructor Description",
          "FULL_REF",
          "full_value",
          true
      );

      assertThat(config.getId()).isEqualTo(id);
      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getDescription()).isEqualTo("Full Constructor Description");
      assertThat(config.getReference()).isEqualTo("FULL_REF");
      assertThat(config.getValue()).isEqualTo("full_value");
      assertThat(config.isActive()).isTrue();
    }
  }

  @Nested
  @DisplayName("Setters")
  class SetterTests {

    @Test
    @DisplayName("should set id")
    void shouldSetId() {
      var config = new SystemConfiguration();
      var id = UUID.randomUUID();

      config.setId(id);

      assertThat(config.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should set group")
    void shouldSetGroup() {
      var config = new SystemConfiguration();
      var group = new SystemConfigurationGroup();
      group.setId(UUID.randomUUID());
      group.setName("New Group");

      config.setGroup(group);

      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getGroup().getName()).isEqualTo("New Group");
    }

    @Test
    @DisplayName("should set description")
    void shouldSetDescription() {
      var config = new SystemConfiguration();

      config.setDescription("New Description");

      assertThat(config.getDescription()).isEqualTo("New Description");
    }

    @Test
    @DisplayName("should set reference")
    void shouldSetReference() {
      var config = new SystemConfiguration();

      config.setReference("NEW_REF");

      assertThat(config.getReference()).isEqualTo("NEW_REF");
    }

    @Test
    @DisplayName("should set value")
    void shouldSetValue() {
      var config = new SystemConfiguration();

      config.setValue("new_value");

      assertThat(config.getValue()).isEqualTo("new_value");
    }

    @Test
    @DisplayName("should set active")
    void shouldSetActive() {
      var config = new SystemConfiguration();

      config.setActive(true);

      assertThat(config.isActive()).isTrue();

      config.setActive(false);

      assertThat(config.isActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("Equality")
  class EqualityTests {

    @Test
    @DisplayName("should consider same id as equal")
    void shouldConsiderSameIdAsEqual() {
      var id = UUID.randomUUID();
      var config1 = SystemConfiguration.builder()
          .id(id)
          .group(new SystemConfigurationGroup())
          .reference("REF1")
          .value("value1")
          .build();

      var config2 = SystemConfiguration.builder()
          .id(id)
          .group(new SystemConfigurationGroup())
          .reference("REF1")
          .value("value1")
          .build();

      assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("should consider different ids as different")
    void shouldConsiderDifferentIdsAsDifferent() {
      var config1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .reference("REF1")
          .value("value1")
          .build();

      var config2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .reference("REF1")
          .value("value1")
          .build();

      assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("should not be equal to null")
    void shouldNotBeEqualToNull() {
      var config = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .reference("REF")
          .value("value")
          .build();

      assertThat(config).isNotEqualTo(null);
    }

    @Test
    @DisplayName("should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
      var config = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .reference("REF")
          .value("value")
          .build();

      assertThat(config).isNotEqualTo("not a config");
    }
  }

}
