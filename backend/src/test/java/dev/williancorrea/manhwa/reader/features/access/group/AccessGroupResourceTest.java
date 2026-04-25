package dev.williancorrea.manhwa.reader.features.access.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessGroupResource")
class AccessGroupResourceTest {

  @Mock
  private AccessGroupService accessGroupService;

  @InjectMocks
  private AccessGroupResource accessGroupResource;

  private ObjectMapper objectMapper;
  private UUID groupId;
  private AccessGroup accessGroup;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
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

      when(accessGroupService.findAll()).thenReturn(groups);

      var response = accessGroupResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .hasSize(2);
      verify(accessGroupService).findAll();
    }

    @Test
    @DisplayName("should return empty list when no groups exist")
    void shouldReturnEmptyListWhenNoGroupsExist() {
      when(accessGroupService.findAll()).thenReturn(List.of());

      var response = accessGroupResource.findAll();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .isEmpty();
      verify(accessGroupService).findAll();
    }

    @Test
    @DisplayName("should convert entities to output DTOs")
    void shouldConvertEntitiesToOutputDTOs() {
      var groups = List.of(accessGroup);

      when(accessGroupService.findAll()).thenReturn(groups);

      var response = accessGroupResource.findAll();

      assertThat(response.getBody())
          .isNotNull()
          .hasSize(1)
          .satisfies(body -> {
            assertThat(body.get(0).getId()).isEqualTo(groupId);
            assertThat(body.get(0).getName()).isEqualTo("READER");
          });
      verify(accessGroupService).findAll();
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create and return access group")
    void shouldCreateAndReturnAccessGroup() {
      var input = new AccessGroupInput("READER");

      when(accessGroupService.save(any(AccessGroup.class))).thenReturn(accessGroup);

      var response = accessGroupResource.create(input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(groupId);
            assertThat(body.getName()).isEqualTo("READER");
          });
      verify(accessGroupService).save(any(AccessGroup.class));
    }

    @Test
    @DisplayName("should convert input to entity")
    void shouldConvertInputToEntity() {
      var input = new AccessGroupInput("MODERATOR");
      var savedGroup = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.MODERATOR)
          .build();

      when(accessGroupService.save(any(AccessGroup.class))).thenReturn(savedGroup);

      var response = accessGroupResource.create(input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getName()).isEqualTo("MODERATOR");
          });
      verify(accessGroupService).save(any(AccessGroup.class));
    }

    @Test
    @DisplayName("should handle all group types")
    void shouldHandleAllGroupTypes() {
      for (var type : GroupType.values()) {
        var input = new AccessGroupInput(type.name());
        var savedGroup = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(type)
            .build();

        when(accessGroupService.save(any(AccessGroup.class))).thenReturn(savedGroup);

        var response = accessGroupResource.create(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(type.name());
      }
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return access group when found")
    void shouldReturnAccessGroupWhenFound() {
      when(accessGroupService.findById(groupId)).thenReturn(Optional.of(accessGroup));

      var response = accessGroupResource.findById(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(groupId);
            assertThat(body.getName()).isEqualTo("READER");
          });
      verify(accessGroupService).findById(groupId);
    }

    @Test
    @DisplayName("should return not found when group does not exist")
    void shouldReturnNotFoundWhenGroupDoesNotExist() {
      when(accessGroupService.findById(groupId)).thenReturn(Optional.empty());

      var response = accessGroupResource.findById(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupService).findById(groupId);
    }

    @Test
    @DisplayName("should convert entity to output DTO")
    void shouldConvertEntityToOutputDTO() {
      when(accessGroupService.findById(groupId)).thenReturn(Optional.of(accessGroup));

      var response = accessGroupResource.findById(groupId);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(accessGroup.getId());
            assertThat(body.getName()).isEqualTo(accessGroup.getName().name());
          });
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update and return access group")
    void shouldUpdateAndReturnAccessGroup() {
      var input = new AccessGroupInput("MODERATOR");
      var updatedGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.MODERATOR)
          .build();

      when(accessGroupService.existsById(groupId)).thenReturn(true);
      when(accessGroupService.save(any(AccessGroup.class))).thenReturn(updatedGroup);

      var response = accessGroupResource.update(groupId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(groupId);
            assertThat(body.getName()).isEqualTo("MODERATOR");
          });
      verify(accessGroupService).existsById(groupId);
      verify(accessGroupService).save(any(AccessGroup.class));
    }

    @Test
    @DisplayName("should return not found when group does not exist")
    void shouldReturnNotFoundWhenGroupDoesNotExist() {
      var input = new AccessGroupInput("MODERATOR");

      when(accessGroupService.existsById(groupId)).thenReturn(false);

      var response = accessGroupResource.update(groupId, input);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupService).existsById(groupId);
    }

    @Test
    @DisplayName("should preserve group id during update")
    void shouldPreserveGroupIdDuringUpdate() {
      var input = new AccessGroupInput("ADMINISTRATOR");
      var updatedGroup = AccessGroup.builder()
          .id(groupId)
          .name(GroupType.ADMINISTRATOR)
          .build();

      when(accessGroupService.existsById(groupId)).thenReturn(true);
      when(accessGroupService.save(any(AccessGroup.class))).thenReturn(updatedGroup);

      var response = accessGroupResource.update(groupId, input);

      assertThat(response.getBody())
          .isNotNull()
          .satisfies(body -> {
            assertThat(body.getId()).isEqualTo(groupId);
          });
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete access group successfully")
    void shouldDeleteAccessGroupSuccessfully() {
      when(accessGroupService.existsById(groupId)).thenReturn(true);

      var response = accessGroupResource.delete(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      verify(accessGroupService).existsById(groupId);
      verify(accessGroupService).deleteById(groupId);
    }

    @Test
    @DisplayName("should return not found when group does not exist")
    void shouldReturnNotFoundWhenGroupDoesNotExist() {
      when(accessGroupService.existsById(groupId)).thenReturn(false);

      var response = accessGroupResource.delete(groupId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNull();
      verify(accessGroupService).existsById(groupId);
    }

    @Test
    @DisplayName("should not call deleteById if group does not exist")
    void shouldNotCallDeleteByIdIfGroupDoesNotExist() {
      when(accessGroupService.existsById(groupId)).thenReturn(false);

      accessGroupResource.delete(groupId);

      verify(accessGroupService).existsById(groupId);
    }
  }

  @Nested
  @DisplayName("toEntity()")
  class ToEntityTests {

    @Test
    @DisplayName("should convert input to entity correctly")
    void shouldConvertInputToEntityCorrectly() {
      var input = new AccessGroupInput("READER");
      var savedEntity = AccessGroup.builder()
          .id(UUID.randomUUID())
          .name(GroupType.READER)
          .build();

      when(accessGroupService.save(any(AccessGroup.class))).thenReturn(savedEntity);

      var response = accessGroupResource.create(input);

      verify(accessGroupService).save(any(AccessGroup.class));
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("should convert all group type inputs to entities")
    void shouldConvertAllGroupTypeInputsToEntities() {
      for (var type : GroupType.values()) {
        var input = new AccessGroupInput(type.name());
        var savedEntity = AccessGroup.builder()
            .id(UUID.randomUUID())
            .name(type)
            .build();

        when(accessGroupService.save(any(AccessGroup.class))).thenReturn(savedEntity);

        var response = accessGroupResource.create(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      }
    }
  }
}
