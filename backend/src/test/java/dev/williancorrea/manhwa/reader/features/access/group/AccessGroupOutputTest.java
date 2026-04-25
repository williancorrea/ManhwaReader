package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroupOutput")
class AccessGroupOutputTest {

  private UUID groupId;
  private AccessGroup accessGroup;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    accessGroup = AccessGroup.builder()
        .id(groupId)
        .name(GroupType.READER)
        .build();
  }

  @Nested
  @DisplayName("Constructor from AccessGroup")
  class ConstructorFromAccessGroupTests {

    @Test
    @DisplayName("should create output from access group")
    void shouldCreateOutputFromAccessGroup() {
      var output = new AccessGroupOutput(accessGroup);

      assertThat(output.getId()).isEqualTo(groupId);
      assertThat(output.getName()).isEqualTo("READER");
    }

    @Test
    @DisplayName("should convert group type to string name")
    void shouldConvertGroupTypeToStringName() {
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();

      var output = new AccessGroupOutput(adminGroup);

      assertThat(output.getName()).isEqualTo("ADMINISTRATOR");
    }

    @Test
    @DisplayName("should handle all group types")
    void shouldHandleAllGroupTypes() {
      for (var type : GroupType.values()) {
        var group = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(type)
            .build();

        var output = new AccessGroupOutput(group);

        assertThat(output.getName()).isEqualTo(type.name());
      }
    }

    @Test
    @DisplayName("should preserve group id")
    void shouldPreserveGroupId() {
      var newId = UUID.randomUUID();
      var group = AccessGroup.builder()
          .id(newId)
          .name(GroupType.MODERATOR)
          .build();

      var output = new AccessGroupOutput(group);

      assertThat(output.getId()).isEqualTo(newId);
    }
  }

  @Nested
  @DisplayName("Builder Constructor")
  class BuilderConstructorTests {

    @Test
    @DisplayName("should create output using builder")
    void shouldCreateOutputUsingBuilder() {
      var output = AccessGroupOutput.builder()
          .id(groupId)
          .name("READER")
          .build();

      assertThat(output.getId()).isEqualTo(groupId);
      assertThat(output.getName()).isEqualTo("READER");
    }

    @Test
    @DisplayName("should create output with only id")
    void shouldCreateOutputWithOnlyId() {
      var output = AccessGroupOutput.builder()
          .id(groupId)
          .build();

      assertThat(output.getId()).isEqualTo(groupId);
      assertThat(output.getName()).isNull();
    }

    @Test
    @DisplayName("should create output with only name")
    void shouldCreateOutputWithOnlyName() {
      var output = AccessGroupOutput.builder()
          .name("ADMINISTRATOR")
          .build();

      assertThat(output.getId()).isNull();
      assertThat(output.getName()).isEqualTo("ADMINISTRATOR");
    }
  }

  @Nested
  @DisplayName("AllArgsConstructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create output with all args")
    void shouldCreateOutputWithAllArgs() {
      var output = new AccessGroupOutput(groupId, "UPLOADER");

      assertThat(output.getId()).isEqualTo(groupId);
      assertThat(output.getName()).isEqualTo("UPLOADER");
    }
  }

  @Nested
  @DisplayName("NoArgsConstructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty output")
    void shouldCreateEmptyOutput() {
      var output = new AccessGroupOutput();

      assertThat(output.getId()).isNull();
      assertThat(output.getName()).isNull();
    }
  }

  @Nested
  @DisplayName("Getters")
  class GettersTests {

    @Test
    @DisplayName("should get id")
    void shouldGetId() {
      var output = new AccessGroupOutput(groupId, "MODERATOR");

      assertThat(output.getId()).isEqualTo(groupId);
    }

    @Test
    @DisplayName("should get name")
    void shouldGetName() {
      var output = new AccessGroupOutput(groupId, "READER");

      assertThat(output.getName()).isEqualTo("READER");
    }
  }

  @Nested
  @DisplayName("Serialization")
  class SerializationTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var output = new AccessGroupOutput(groupId, "READER");

      assertThat(output).isInstanceOf(java.io.Serializable.class);
    }
  }

  @Nested
  @DisplayName("Multiple Conversions")
  class MultipleConversionsTests {

    @Test
    @DisplayName("should convert multiple access groups correctly")
    void shouldConvertMultipleAccessGroupsCorrectly() {
      var group1 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var group2 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var group3 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();

      var output1 = new AccessGroupOutput(group1);
      var output2 = new AccessGroupOutput(group2);
      var output3 = new AccessGroupOutput(group3);

      assertThat(output1.getName()).isEqualTo("ADMINISTRATOR");
      assertThat(output2.getName()).isEqualTo("MODERATOR");
      assertThat(output3.getName()).isEqualTo("READER");
      assertThat(output1.getId()).isEqualTo(group1.getId());
      assertThat(output2.getId()).isEqualTo(group2.getId());
      assertThat(output3.getId()).isEqualTo(group3.getId());
    }
  }
}
