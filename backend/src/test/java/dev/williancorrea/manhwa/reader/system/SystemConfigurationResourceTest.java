package dev.williancorrea.manhwa.reader.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SystemConfigurationResourceTest {

  @Mock
  private SystemConfigurationService systemConfigurationService;

  @InjectMocks
  private SystemConfigurationResource systemConfigurationResource;

  @Nested
  class FindAllTests {

    @Test
    void givenNoConfigurations_whenFindAll_thenReturnEmptyList() {
      // Arrange
      when(systemConfigurationService.findAll()).thenReturn(Collections.emptyList());

      // Act
      ResponseEntity<List<SystemConfigurationOutput>> response = systemConfigurationResource.findAll();

      // Assert
      assertNotNull(response);
      assertEquals(200, response.getStatusCode().value());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isEmpty());
      verify(systemConfigurationService, times(1)).findAll();
    }

    @Test
    void givenConfigurationsExist_whenFindAll_thenReturnConfigurationsList() {
      // Arrange
      var entity1 = SystemConfiguration.builder()
          .reference("key1")
          .value("value1")
          .description("key1 description")
          .group(SystemConfigurationGroup.builder()
              .name("MINIO")
              .build())
          .build();

      var entity2 = SystemConfiguration.builder()
          .reference("key2")
          .value("value2")
          .description("key2 description")
          .group(SystemConfigurationGroup.builder()
              .name("CONFIG")
              .build())
          .build();

      when(systemConfigurationService.findAll()).thenReturn(List.of(entity1, entity2));

      // Act
      ResponseEntity<List<SystemConfigurationOutput>> response = systemConfigurationResource.findAll();

      // Assert
      assertNotNull(response);
      assertEquals(200, response.getStatusCode().value());
      assertNotNull(response.getBody());
      assertEquals(2, response.getBody().size());
      assertEquals("key1", response.getBody().get(0).getReference());
      assertEquals("value1", response.getBody().get(0).getValue());
      assertEquals("key2", response.getBody().get(1).getReference());
      assertEquals("value2", response.getBody().get(1).getValue());
      verify(systemConfigurationService, times(1)).findAll();
    }
  }

  @Nested
  class FindAllByGroupTests {

    @Test
    void givenNoConfigurationsForGroup_whenFindAllByGroup_thenReturnEmptyList() {
      // Arrange
      var group = SystemConfigurationGroup.builder().name("UNKNOWN_GROUP").build();
      when(systemConfigurationService.findAllByGroup(group)).thenReturn(Collections.emptyList());

      // Act
      ResponseEntity<List<SystemConfigurationOutput>> response = systemConfigurationResource.findAllByGroup(group.getId());

      // Assert
      assertNotNull(response);
      assertEquals(200, response.getStatusCode().value());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isEmpty());
      verify(systemConfigurationService, times(1)).findAllByGroup(group);
    }

    @Test
    void givenConfigurationsForGroup_whenFindAllByGroup_thenReturnConfigurationsList() {
      // Arrange
      var group = SystemConfigurationGroup.builder().name("MINIO").build();

      var entity1 = SystemConfiguration.builder()
          .reference("key1")
          .value("value1")
          .description("key1 description")
          .group(group)
          .build();

      var entity2 = SystemConfiguration.builder()
          .reference("key2")
          .value("value2")
          .description("key2 description")
          .group(group)
          .build();

      when(systemConfigurationService.findAllByGroup(group)).thenReturn(List.of(entity1, entity2));

      // Act
      ResponseEntity<List<SystemConfigurationOutput>> response = systemConfigurationResource.findAllByGroup(group.getId());

      // Assert
      assertNotNull(response);
      assertEquals(200, response.getStatusCode().value());
      assertNotNull(response.getBody());
      assertEquals(2, response.getBody().size());
      assertEquals("key1", response.getBody().get(0).getReference());
      assertEquals("value1", response.getBody().get(0).getValue());
      assertEquals("key2", response.getBody().get(1).getReference());
      assertEquals("value2", response.getBody().get(1).getValue());
      verify(systemConfigurationService, times(1)).findAllByGroup(group);
    }

    @Test
    void givenNullGroup_whenFindAllByGroup_thenThrowIllegalArgumentException() {
      // Arrange
      when(systemConfigurationService.findAllByGroup(any())).thenThrow(IllegalArgumentException.class);

      // Act / Assert
      assertThrows(IllegalArgumentException.class, () -> systemConfigurationResource.findAllByGroup(null));

      verify(systemConfigurationService, times(1)).findAllByGroup(any());
    }
  }
}