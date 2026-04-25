package dev.williancorrea.manhwa.reader.system;

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
@DisplayName("SystemConfigurationGroupService")
class SystemConfigurationGroupServiceTest {

  @Mock
  private SystemConfigurationGroupRepository repository;

  @InjectMocks
  private SystemConfigurationGroupService service;

  private UUID groupId;
  private String groupName;
  private SystemConfigurationGroup testGroup;

  @BeforeEach
  void setUp() {
    groupId = UUID.randomUUID();
    groupName = "Test Group";
    testGroup = SystemConfigurationGroup.builder()
        .id(groupId)
        .name(groupName)
        .description("Test Description")
        .active(true)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return empty list when no groups exist")
    void shouldReturnEmptyListWhenNoGroupsExist() {
      when(repository.findAll()).thenReturn(List.of());

      var result = service.findAll();

      assertThat(result).isEmpty();
      verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return list of groups when groups exist")
    void shouldReturnListOfGroupsWhenGroupsExist() {
      var group1 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 1")
          .active(true)
          .build();

      var group2 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 2")
          .active(false)
          .build();

      var groups = List.of(group1, group2);
      when(repository.findAll()).thenReturn(groups);

      var result = service.findAll();

      assertThat(result).hasSize(2);
      assertThat(result).containsExactly(group1, group2);
      verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return list with single group")
    void shouldReturnListWithSingleGroup() {
      when(repository.findAll()).thenReturn(List.of(testGroup));

      var result = service.findAll();

      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isEqualTo(testGroup);
      verify(repository, times(1)).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return group when found by id")
    void shouldReturnGroupWhenFoundById() {
      when(repository.findById(groupId)).thenReturn(Optional.of(testGroup));

      var result = service.findById(groupId);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testGroup);
      assertThat(result.get().getId()).isEqualTo(groupId);
      verify(repository, times(1)).findById(groupId);
    }

    @Test
    @DisplayName("should return empty optional when group not found")
    void shouldReturnEmptyOptionalWhenGroupNotFound() {
      var nonExistentId = UUID.randomUUID();
      when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

      var result = service.findById(nonExistentId);

      assertThat(result).isEmpty();
      verify(repository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("should return group with specific id")
    void shouldReturnGroupWithSpecificId() {
      var specificId = UUID.randomUUID();
      var specificGroup = SystemConfigurationGroup.builder()
          .id(specificId)
          .name("Specific Group")
          .active(true)
          .build();

      when(repository.findById(specificId)).thenReturn(Optional.of(specificGroup));

      var result = service.findById(specificId);

      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(specificId);
      assertThat(result.get().getName()).isEqualTo("Specific Group");
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save group successfully")
    void shouldSaveGroupSuccessfully() {
      when(repository.save(any(SystemConfigurationGroup.class))).thenReturn(testGroup);

      var result = service.save(testGroup);

      assertThat(result).isNotNull();
      assertThat(result).isEqualTo(testGroup);
      assertThat(result.getId()).isEqualTo(groupId);
      assertThat(result.getName()).isEqualTo(groupName);
      verify(repository, times(1)).save(testGroup);
    }

    @Test
    @DisplayName("should save group with all fields")
    void shouldSaveGroupWithAllFields() {
      var fullGroup = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Full Group")
          .description("Complete Description")
          .active(true)
          .build();

      when(repository.save(any(SystemConfigurationGroup.class))).thenReturn(fullGroup);

      var result = service.save(fullGroup);

      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("Full Group");
      assertThat(result.getDescription()).isEqualTo("Complete Description");
      assertThat(result.isActive()).isTrue();
      verify(repository).save(fullGroup);
    }

    @Test
    @DisplayName("should save group without description")
    void shouldSaveGroupWithoutDescription() {
      var groupWithoutDesc = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("No Description Group")
          .active(false)
          .build();

      when(repository.save(any(SystemConfigurationGroup.class))).thenReturn(groupWithoutDesc);

      var result = service.save(groupWithoutDesc);

      assertThat(result).isNotNull();
      assertThat(result.getDescription()).isNull();
      assertThat(result.isActive()).isFalse();
      verify(repository).save(groupWithoutDesc);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when group exists")
    void shouldReturnTrueWhenGroupExists() {
      when(repository.existsById(groupId)).thenReturn(true);

      var result = service.existsById(groupId);

      assertThat(result).isTrue();
      verify(repository, times(1)).existsById(groupId);
    }

    @Test
    @DisplayName("should return false when group does not exist")
    void shouldReturnFalseWhenGroupDoesNotExist() {
      var nonExistentId = UUID.randomUUID();
      when(repository.existsById(nonExistentId)).thenReturn(false);

      var result = service.existsById(nonExistentId);

      assertThat(result).isFalse();
      verify(repository, times(1)).existsById(nonExistentId);
    }

    @Test
    @DisplayName("should check existence for multiple ids")
    void shouldCheckExistenceForMultipleIds() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();

      when(repository.existsById(id1)).thenReturn(true);
      when(repository.existsById(id2)).thenReturn(false);

      assertThat(service.existsById(id1)).isTrue();
      assertThat(service.existsById(id2)).isFalse();
      verify(repository).existsById(id1);
      verify(repository).existsById(id2);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete group by id")
    void shouldDeleteGroupById() {
      service.deleteById(groupId);

      verify(repository, times(1)).deleteById(groupId);
    }

    @Test
    @DisplayName("should delete multiple groups")
    void shouldDeleteMultipleGroups() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();

      service.deleteById(id1);
      service.deleteById(id2);

      verify(repository).deleteById(id1);
      verify(repository).deleteById(id2);
    }

    @Test
    @DisplayName("should call repository delete for non-existent id")
    void shouldCallRepositoryDeleteForNonExistentId() {
      var nonExistentId = UUID.randomUUID();

      service.deleteById(nonExistentId);

      verify(repository, times(1)).deleteById(nonExistentId);
    }
  }
}
