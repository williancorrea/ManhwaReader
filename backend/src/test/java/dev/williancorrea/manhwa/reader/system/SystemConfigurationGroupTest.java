package dev.williancorrea.manhwa.reader.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SystemConfigurationGroup")
class SystemConfigurationGroupTest {

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should create system configuration group with all fields")
    void shouldCreateSystemConfigurationGroupWithAllFields() {
      var id = UUID.randomUUID();

      var group = SystemConfigurationGroup.builder()
          .id(id)
          .name("Test Group")
          .description("Test Description")
          .active(true)
          .build();

      assertThat(group).isNotNull();
      assertThat(group.getId()).isEqualTo(id);
      assertThat(group.getName()).isEqualTo("Test Group");
      assertThat(group.getDescription()).isEqualTo("Test Description");
      assertThat(group.isActive()).isTrue();
    }

    @Test
    @DisplayName("should create system configuration group with minimum required fields")
    void shouldCreateSystemConfigurationGroupWithMinimumFields() {
      var id = UUID.randomUUID();

      var group = SystemConfigurationGroup.builder()
          .id(id)
          .name("Minimal Group")
          .active(false)
          .build();

      assertThat(group).isNotNull();
      assertThat(group.getId()).isEqualTo(id);
      assertThat(group.getName()).isEqualTo("Minimal Group");
      assertThat(group.getDescription()).isNull();
      assertThat(group.isActive()).isFalse();
    }

    @Test
    @DisplayName("should create system configuration group without id")
    void shouldCreateSystemConfigurationGroupWithoutId() {
      var group = SystemConfigurationGroup.builder()
          .name("No ID Group")
          .description("Description without ID")
          .active(true)
          .build();

      assertThat(group).isNotNull();
      assertThat(group.getId()).isNull();
      assertThat(group.getName()).isEqualTo("No ID Group");
      assertThat(group.getDescription()).isEqualTo("Description without ID");
    }
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should create system configuration group with no-args constructor")
    void shouldCreateSystemConfigurationGroupWithNoArgsConstructor() {
      var group = new SystemConfigurationGroup();

      assertThat(group).isNotNull();
      assertThat(group.getId()).isNull();
      assertThat(group.getName()).isNull();
      assertThat(group.getDescription()).isNull();
      assertThat(group.isActive()).isFalse();
    }

    @Test
    @DisplayName("should create system configuration group with all-args constructor")
    void shouldCreateSystemConfigurationGroupWithAllArgsConstructor() {
      var id = UUID.randomUUID();

      var group = new SystemConfigurationGroup(
          id,
          "Full Constructor Group",
          "Full Description",
          true
      );

      assertThat(group.getId()).isEqualTo(id);
      assertThat(group.getName()).isEqualTo("Full Constructor Group");
      assertThat(group.getDescription()).isEqualTo("Full Description");
      assertThat(group.isActive()).isTrue();
    }
  }

  @Nested
  @DisplayName("Setters")
  class SetterTests {

    @Test
    @DisplayName("should set id")
    void shouldSetId() {
      var group = new SystemConfigurationGroup();
      var id = UUID.randomUUID();

      group.setId(id);

      assertThat(group.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should set name")
    void shouldSetName() {
      var group = new SystemConfigurationGroup();

      group.setName("Updated Group Name");

      assertThat(group.getName()).isEqualTo("Updated Group Name");
    }

    @Test
    @DisplayName("should set description")
    void shouldSetDescription() {
      var group = new SystemConfigurationGroup();

      group.setDescription("Updated Description");

      assertThat(group.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("should set active")
    void shouldSetActive() {
      var group = new SystemConfigurationGroup();

      group.setActive(true);

      assertThat(group.isActive()).isTrue();

      group.setActive(false);

      assertThat(group.isActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("Equality")
  class EqualityTests {

    @Test
    @DisplayName("should consider same id as equal")
    void shouldConsiderSameIdAsEqual() {
      var id = UUID.randomUUID();
      var group1 = SystemConfigurationGroup.builder()
          .id(id)
          .name("Group 1")
          .active(true)
          .build();

      var group2 = SystemConfigurationGroup.builder()
          .id(id)
          .name("Group 1")
          .active(true)
          .build();

      assertThat(group1).isEqualTo(group2);
    }

    @Test
    @DisplayName("should consider different ids as different")
    void shouldConsiderDifferentIdsAsDifferent() {
      var group1 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 1")
          .active(true)
          .build();

      var group2 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 1")
          .active(true)
          .build();

      assertThat(group1).isNotEqualTo(group2);
    }

    @Test
    @DisplayName("should not be equal to null")
    void shouldNotBeEqualToNull() {
      var group = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group")
          .active(true)
          .build();

      assertThat(group).isNotEqualTo(null);
    }

    @Test
    @DisplayName("should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
      var group = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group")
          .active(true)
          .build();

      assertThat(group).isNotEqualTo("not a group");
    }
  }

}
