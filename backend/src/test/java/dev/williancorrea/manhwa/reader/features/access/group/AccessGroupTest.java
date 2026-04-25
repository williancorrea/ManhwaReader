package dev.williancorrea.manhwa.reader.features.access.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessGroup")
class AccessGroupTest {

  private UUID groupId;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("AccessGroup Builder")
  class AccessGroupBuilderTests {

    @Test
    @DisplayName("should create access group with id and name")
    void shouldCreateAccessGroupWithIdAndName() {
      var accessGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();

      assertThat(accessGroup.getId()).isEqualTo(groupId);
      assertThat(accessGroup.getName()).isEqualTo(GroupType.READER);
    }

    @Test
    @DisplayName("should create access group with all group types")
    void shouldCreateAccessGroupWithAllGroupTypes() {
      for (var type : GroupType.values()) {
        var accessGroup = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(type)
            .build();

        assertThat(accessGroup.getName()).isEqualTo(type);
      }
    }
  }

  @Nested
  @DisplayName("AccessGroup properties")
  class AccessGroupPropertiesTests {

    @Test
    @DisplayName("should support getters and setters")
    void shouldSupportGettersAndSetters() {
      var accessGroup = new AccessGroup();
      accessGroup.setId(groupId);
      accessGroup.setName(GroupType.ADMINISTRATOR);

      assertThat(accessGroup.getId()).isEqualTo(groupId);
      assertThat(accessGroup.getName()).isEqualTo(GroupType.ADMINISTRATOR);
    }

    @Test
    @DisplayName("should support all group types")
    void shouldSupportAllGroupTypes() {
      var adminGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var moderatorGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var readerGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();
      var uploaderGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();

      assertThat(adminGroup.getName()).isEqualTo(GroupType.ADMINISTRATOR);
      assertThat(moderatorGroup.getName()).isEqualTo(GroupType.MODERATOR);
      assertThat(readerGroup.getName()).isEqualTo(GroupType.READER);
      assertThat(uploaderGroup.getName()).isEqualTo(GroupType.UPLOADER);
    }
  }

  @Nested
  @DisplayName("AccessGroup equality")
  class AccessGroupEqualityTests {

    @Test
    @DisplayName("should be equal when ids are the same")
    void shouldBeEqualWhenIdsAreSame() {
      var accessGroup1 = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();
      var accessGroup2 = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();

      assertThat(accessGroup1).isEqualTo(accessGroup2);
    }

    @Test
    @DisplayName("should not be equal when ids differ")
    void shouldNotBeEqualWhenIdsDiffer() {
      var accessGroup1 = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();
      var accessGroup2 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();

      assertThat(accessGroup1).isNotEqualTo(accessGroup2);
    }

    @Test
    @DisplayName("should have same hash code when ids are the same")
    void shouldHaveSameHashCodeWhenIdsAreSame() {
      var accessGroup1 = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();
      var accessGroup2 = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.READER)
          .build();

      assertThat(accessGroup1).hasSameHashCodeAs(accessGroup2);
    }
  }
}
