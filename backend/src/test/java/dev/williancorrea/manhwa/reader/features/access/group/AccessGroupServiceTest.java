package dev.williancorrea.manhwa.reader.features.access.group;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessGroupService")
class AccessGroupServiceTest {

  @Mock
  private AccessGroupRepository accessGroupRepository;

  @InjectMocks
  private AccessGroupService accessGroupService;

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
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all access groups")
    void shouldReturnAllAccessGroups() {
      var group2 = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var groups = List.of(accessGroup, group2);

      when(accessGroupRepository.findAll()).thenReturn(groups);

      var result = accessGroupService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(accessGroup, group2);
      verify(accessGroupRepository).findAll();
    }

    @Test
    @DisplayName("should return all group types")
    void shouldReturnAllGroupTypes() {
      var admin = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.ADMINISTRATOR)
          .build();
      var moderator = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();
      var reader = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();
      var uploader = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.UPLOADER)
          .build();
      var groups = List.of(admin, moderator, reader, uploader);

      when(accessGroupRepository.findAll()).thenReturn(groups);

      var result = accessGroupService.findAll();

      assertThat(result)
          .hasSize(4)
          .extracting(AccessGroup::getName)
          .contains(
              GroupType.ADMINISTRATOR,
              GroupType.MODERATOR,
              GroupType.READER,
              GroupType.UPLOADER
          );
      verify(accessGroupRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no groups exist")
    void shouldReturnEmptyList() {
      when(accessGroupRepository.findAll()).thenReturn(List.of());

      var result = accessGroupService.findAll();

      assertThat(result).isEmpty();
      verify(accessGroupRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return access group when found")
    void shouldReturnAccessGroupWhenFound() {
      when(accessGroupRepository.findById(groupId)).thenReturn(Optional.of(accessGroup));

      var result = accessGroupService.findById(groupId);

      assertThat(result)
          .isPresent()
          .contains(accessGroup);
      verify(accessGroupRepository).findById(groupId);
    }

    @Test
    @DisplayName("should return empty optional when group not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(accessGroupRepository.findById(groupId)).thenReturn(Optional.empty());

      var result = accessGroupService.findById(groupId);

      assertThat(result).isEmpty();
      verify(accessGroupRepository).findById(groupId);
    }

    @Test
    @DisplayName("should return correct access group by id")
    void shouldReturnCorrectAccessGroupById() {
      var adminGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.ADMINISTRATOR)
          .build();

      when(accessGroupRepository.findById(groupId)).thenReturn(Optional.of(adminGroup));

      var result = accessGroupService.findById(groupId);

      assertThat(result)
          .isPresent()
          .get()
          .satisfies(g -> {
            assertThat(g.getId()).isEqualTo(groupId);
            assertThat(g.getName()).isEqualTo(GroupType.ADMINISTRATOR);
          });
      verify(accessGroupRepository).findById(groupId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save and return access group")
    void shouldSaveAndReturnAccessGroup() {
      when(accessGroupRepository.save(accessGroup)).thenReturn(accessGroup);

      var result = accessGroupService.save(accessGroup);

      assertThat(result)
          .isNotNull()
          .isEqualTo(accessGroup);
      verify(accessGroupRepository).save(accessGroup);
    }

    @Test
    @DisplayName("should save new access group and assign id")
    void shouldSaveNewAccessGroupAndAssignId() {
      var newGroup = AccessGroup.builder()
          .name(GroupType.MODERATOR)
          .build();
      var savedGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();

      when(accessGroupRepository.save(newGroup)).thenReturn(savedGroup);

      var result = accessGroupService.save(newGroup);

      assertThat(result.getId()).isNotNull();
      verify(accessGroupRepository).save(newGroup);
    }

    @Test
    @DisplayName("should update existing access group")
    void shouldUpdateExistingAccessGroup() {
      var updatedGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.MODERATOR)
          .build();

      when(accessGroupRepository.save(updatedGroup)).thenReturn(updatedGroup);

      var result = accessGroupService.save(updatedGroup);

      assertThat(result.getName()).isEqualTo(GroupType.MODERATOR);
      verify(accessGroupRepository).save(updatedGroup);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when access group exists")
    void shouldReturnTrueWhenAccessGroupExists() {
      when(accessGroupRepository.existsById(groupId)).thenReturn(true);

      var result = accessGroupService.existsById(groupId);

      assertThat(result).isTrue();
      verify(accessGroupRepository).existsById(groupId);
    }

    @Test
    @DisplayName("should return false when access group does not exist")
    void shouldReturnFalseWhenAccessGroupDoesNotExist() {
      when(accessGroupRepository.existsById(groupId)).thenReturn(false);

      var result = accessGroupService.existsById(groupId);

      assertThat(result).isFalse();
      verify(accessGroupRepository).existsById(groupId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete access group by id")
    void shouldDeleteAccessGroupById() {
      accessGroupService.deleteById(groupId);

      verify(accessGroupRepository).deleteById(groupId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent access group")
    void shouldHandleDeletionOfNonExistentAccessGroup() {
      var nonExistentId = UUID.randomUUID();

      accessGroupService.deleteById(nonExistentId);

      verify(accessGroupRepository).deleteById(nonExistentId);
    }
  }
}
