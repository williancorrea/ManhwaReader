package dev.williancorrea.manhwa.reader.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SystemConfigurationServiceTest {

  @Mock
  private SystemConfigurationRepository systemConfigRepo;

  @InjectMocks
  private SystemConfigurationService systemConfigurationService;

  @SuppressWarnings("resource")
  public SystemConfigurationServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class FindAllTests {

    @Test
    void givenNoConfigurations_whenFindAll_thenReturnEmptyList() {
      when(systemConfigRepo.findAll()).thenReturn(Collections.emptyList());

      List<SystemConfiguration> result = systemConfigurationService.findAll();

      assertTrue(result.isEmpty());
      verify(systemConfigRepo, times(1)).findAll();
    }

    @Test
    void givenConfigurationsExist_whenFindAll_thenReturnListOfConfigurations() {
      SystemConfiguration configuration1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .description("Test Description 1")
          .reference("Test Reference 1")
          .value("Value 1")
          .build();

      SystemConfiguration configuration2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(new SystemConfigurationGroup())
          .description("Test Description 2")
          .reference("Test Reference 2")
          .value("Value 2")
          .build();

      List<SystemConfiguration> configurations = List.of(configuration1, configuration2);

      when(systemConfigRepo.findAll()).thenReturn(configurations);

      List<SystemConfiguration> result = systemConfigurationService.findAll();

      assertEquals(2, result.size());
      assertEquals(configuration1, result.get(0));
      assertEquals(configuration2, result.get(1));
      verify(systemConfigRepo, times(1)).findAll();
    }
  }

  @Nested
  class FindAllByGroupTests {

    @Test
    void givenNoConfigurationsInGroup_whenFindAllByGroup_thenReturnEmptyList() {
      SystemConfigurationGroup group = new SystemConfigurationGroup();
      group.setId(UUID.randomUUID());
      when(systemConfigRepo.findAllByGroup(group)).thenReturn(Collections.emptyList());

      List<SystemConfiguration> result = systemConfigurationService.findAllByGroup(group);

      assertTrue(result.isEmpty());
      verify(systemConfigRepo, times(1)).findAllByGroup(group);
    }

    @Test
    void givenConfigurationsInGroup_whenFindAllByGroup_thenReturnConfigurations() {
      // Arrange
      SystemConfigurationGroup group = new SystemConfigurationGroup();
      group.setId(UUID.randomUUID());
      SystemConfiguration configuration1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(group)
          .description("Config 1")
          .reference("ref1")
          .value("value1")
          .build();

      SystemConfiguration configuration2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(group)
          .description("Config 2")
          .reference("ref2")
          .value("value2")
          .build();

      when(systemConfigRepo.findAllByGroup(group)).thenReturn(List.of(configuration1, configuration2));

      // Act
      List<SystemConfiguration> result = systemConfigurationService.findAllByGroup(group);

      // Assert
      assertEquals(2, result.size());
      assertEquals(configuration1, result.get(0));
      assertEquals(configuration2, result.get(1));
      verify(systemConfigRepo, times(1)).findAllByGroup(group);
    }
  }
}