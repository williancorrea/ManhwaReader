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
@DisplayName("SystemConfigurationService")
class SystemConfigurationServiceTest {

  @Mock
  private SystemConfigurationRepository systemConfigRepo;

  @InjectMocks
  private SystemConfigurationService service;

  private UUID configId;
  private UUID groupId;
  private String reference;
  private SystemConfigurationGroup testGroup;
  private SystemConfiguration testConfig;

  @BeforeEach
  void setUp() {
    configId = UUID.randomUUID();
    groupId = UUID.randomUUID();
    reference = "TEST_REFERENCE";

    testGroup = SystemConfigurationGroup.builder()
        .id(groupId)
        .name("Test Group")
        .active(true)
        .build();

    testConfig = SystemConfiguration.builder()
        .id(configId)
        .group(testGroup)
        .reference(reference)
        .value("test_value")
        .active(true)
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return empty list when no configurations exist")
    void shouldReturnEmptyListWhenNoConfigurationsExist() {
      when(systemConfigRepo.findAll()).thenReturn(List.of());

      var result = service.findAll();

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("should return list of configurations when configurations exist")
    void shouldReturnListOfConfigurationsWhenConfigurationsExist() {
      var config1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("REF1")
          .value("value1")
          .build();

      var config2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("REF2")
          .value("value2")
          .build();

      var configs = List.of(config1, config2);
      when(systemConfigRepo.findAll()).thenReturn(configs);

      var result = service.findAll();

      assertThat(result).hasSize(2);
      assertThat(result).containsExactly(config1, config2);
      verify(systemConfigRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("should return list with single configuration")
    void shouldReturnListWithSingleConfiguration() {
      when(systemConfigRepo.findAll()).thenReturn(List.of(testConfig));

      var result = service.findAll();

      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isEqualTo(testConfig);
      verify(systemConfigRepo, times(1)).findAll();
    }
  }

  @Nested
  @DisplayName("findAllByGroup()")
  class FindAllByGroupTests {

    @Test
    @DisplayName("should return empty list when no configurations in group")
    void shouldReturnEmptyListWhenNoConfigurationsInGroup() {
      when(systemConfigRepo.findAllByGroup(testGroup)).thenReturn(List.of());

      var result = service.findAllByGroup(testGroup);

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findAllByGroup(testGroup);
    }

    @Test
    @DisplayName("should return configurations for specified group")
    void shouldReturnConfigurationsForSpecifiedGroup() {
      var config1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("GROUP_REF1")
          .value("value1")
          .build();

      var config2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("GROUP_REF2")
          .value("value2")
          .build();

      when(systemConfigRepo.findAllByGroup(testGroup)).thenReturn(List.of(config1, config2));

      var result = service.findAllByGroup(testGroup);

      assertThat(result).hasSize(2);
      assertThat(result).containsExactly(config1, config2);
      verify(systemConfigRepo, times(1)).findAllByGroup(testGroup);
    }

    @Test
    @DisplayName("should return single configuration for group")
    void shouldReturnSingleConfigurationForGroup() {
      when(systemConfigRepo.findAllByGroup(testGroup)).thenReturn(List.of(testConfig));

      var result = service.findAllByGroup(testGroup);

      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isEqualTo(testConfig);
      assertThat(result.get(0).getGroup()).isEqualTo(testGroup);
      verify(systemConfigRepo, times(1)).findAllByGroup(testGroup);
    }

    @Test
    @DisplayName("should find configurations for different groups")
    void shouldFindConfigurationsForDifferentGroups() {
      var group1 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 1")
          .build();

      var group2 = SystemConfigurationGroup.builder()
          .id(UUID.randomUUID())
          .name("Group 2")
          .build();

      var configForGroup1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(group1)
          .reference("REF1")
          .value("val1")
          .build();

      when(systemConfigRepo.findAllByGroup(group1)).thenReturn(List.of(configForGroup1));
      when(systemConfigRepo.findAllByGroup(group2)).thenReturn(List.of());

      assertThat(service.findAllByGroup(group1)).hasSize(1);
      assertThat(service.findAllByGroup(group2)).isEmpty();
      verify(systemConfigRepo).findAllByGroup(group1);
      verify(systemConfigRepo).findAllByGroup(group2);
    }
  }

  @Nested
  @DisplayName("findByGroupName()")
  class FindByGroupNameTests {

    @Test
    @DisplayName("should return empty list when group name not found")
    void shouldReturnEmptyListWhenGroupNameNotFound() {
      when(systemConfigRepo.findByGroupName("NonExistent")).thenReturn(List.of());

      var result = service.findByGroupName("NonExistent");

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findByGroupName("NonExistent");
    }

    @Test
    @DisplayName("should return configurations for group name")
    void shouldReturnConfigurationsForGroupName() {
      var config1 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("NAME_REF1")
          .value("value1")
          .build();

      var config2 = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("NAME_REF2")
          .value("value2")
          .build();

      when(systemConfigRepo.findByGroupName("Test Group")).thenReturn(List.of(config1, config2));

      var result = service.findByGroupName("Test Group");

      assertThat(result).hasSize(2);
      assertThat(result).containsExactly(config1, config2);
      verify(systemConfigRepo, times(1)).findByGroupName("Test Group");
    }

    @Test
    @DisplayName("should return single configuration for group name")
    void shouldReturnSingleConfigurationForGroupName() {
      when(systemConfigRepo.findByGroupName("Test Group")).thenReturn(List.of(testConfig));

      var result = service.findByGroupName("Test Group");

      assertThat(result).hasSize(1);
      assertThat(result.get(0)).isEqualTo(testConfig);
      assertThat(result.get(0).getGroup().getName()).isEqualTo("Test Group");
      verify(systemConfigRepo, times(1)).findByGroupName("Test Group");
    }

    @Test
    @DisplayName("should be case sensitive for group name")
    void shouldBeCaseSensitiveForGroupName() {
      when(systemConfigRepo.findByGroupName("Test Group")).thenReturn(List.of(testConfig));
      when(systemConfigRepo.findByGroupName("test group")).thenReturn(List.of());

      assertThat(service.findByGroupName("Test Group")).hasSize(1);
      assertThat(service.findByGroupName("test group")).isEmpty();
      verify(systemConfigRepo).findByGroupName("Test Group");
      verify(systemConfigRepo).findByGroupName("test group");
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return configuration when found by id")
    void shouldReturnConfigurationWhenFoundById() {
      when(systemConfigRepo.findById(configId)).thenReturn(Optional.of(testConfig));

      var result = service.findById(configId);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testConfig);
      assertThat(result.get().getId()).isEqualTo(configId);
      verify(systemConfigRepo, times(1)).findById(configId);
    }

    @Test
    @DisplayName("should return empty optional when configuration not found")
    void shouldReturnEmptyOptionalWhenConfigurationNotFound() {
      var nonExistentId = UUID.randomUUID();
      when(systemConfigRepo.findById(nonExistentId)).thenReturn(Optional.empty());

      var result = service.findById(nonExistentId);

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("should find configuration with specific id")
    void shouldFindConfigurationWithSpecificId() {
      var specificId = UUID.randomUUID();
      var specificConfig = SystemConfiguration.builder()
          .id(specificId)
          .group(testGroup)
          .reference("SPECIFIC_REF")
          .value("specific_value")
          .build();

      when(systemConfigRepo.findById(specificId)).thenReturn(Optional.of(specificConfig));

      var result = service.findById(specificId);

      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(specificId);
      assertThat(result.get().getReference()).isEqualTo("SPECIFIC_REF");
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save configuration successfully")
    void shouldSaveConfigurationSuccessfully() {
      when(systemConfigRepo.save(any(SystemConfiguration.class))).thenReturn(testConfig);

      var result = service.save(testConfig);

      assertThat(result).isNotNull();
      assertThat(result).isEqualTo(testConfig);
      assertThat(result.getId()).isEqualTo(configId);
      assertThat(result.getReference()).isEqualTo(reference);
      verify(systemConfigRepo, times(1)).save(testConfig);
    }

    @Test
    @DisplayName("should save configuration with all fields")
    void shouldSaveConfigurationWithAllFields() {
      var fullConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .description("Full Description")
          .reference("FULL_REF")
          .value("full_value")
          .active(true)
          .build();

      when(systemConfigRepo.save(any(SystemConfiguration.class))).thenReturn(fullConfig);

      var result = service.save(fullConfig);

      assertThat(result).isNotNull();
      assertThat(result.getDescription()).isEqualTo("Full Description");
      assertThat(result.getReference()).isEqualTo("FULL_REF");
      assertThat(result.getValue()).isEqualTo("full_value");
      assertThat(result.isActive()).isTrue();
      verify(systemConfigRepo).save(fullConfig);
    }

    @Test
    @DisplayName("should save configuration without description")
    void shouldSaveConfigurationWithoutDescription() {
      var configWithoutDesc = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("NO_DESC_REF")
          .value("no_desc_value")
          .active(false)
          .build();

      when(systemConfigRepo.save(any(SystemConfiguration.class))).thenReturn(configWithoutDesc);

      var result = service.save(configWithoutDesc);

      assertThat(result).isNotNull();
      assertThat(result.getDescription()).isNull();
      assertThat(result.isActive()).isFalse();
      verify(systemConfigRepo).save(configWithoutDesc);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when configuration exists")
    void shouldReturnTrueWhenConfigurationExists() {
      when(systemConfigRepo.existsById(configId)).thenReturn(true);

      var result = service.existsById(configId);

      assertThat(result).isTrue();
      verify(systemConfigRepo, times(1)).existsById(configId);
    }

    @Test
    @DisplayName("should return false when configuration does not exist")
    void shouldReturnFalseWhenConfigurationDoesNotExist() {
      var nonExistentId = UUID.randomUUID();
      when(systemConfigRepo.existsById(nonExistentId)).thenReturn(false);

      var result = service.existsById(nonExistentId);

      assertThat(result).isFalse();
      verify(systemConfigRepo, times(1)).existsById(nonExistentId);
    }

    @Test
    @DisplayName("should check existence for multiple ids")
    void shouldCheckExistenceForMultipleIds() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();

      when(systemConfigRepo.existsById(id1)).thenReturn(true);
      when(systemConfigRepo.existsById(id2)).thenReturn(false);

      assertThat(service.existsById(id1)).isTrue();
      assertThat(service.existsById(id2)).isFalse();
      verify(systemConfigRepo).existsById(id1);
      verify(systemConfigRepo).existsById(id2);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete configuration by id")
    void shouldDeleteConfigurationById() {
      service.deleteById(configId);

      verify(systemConfigRepo, times(1)).deleteById(configId);
    }

    @Test
    @DisplayName("should delete multiple configurations")
    void shouldDeleteMultipleConfigurations() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();

      service.deleteById(id1);
      service.deleteById(id2);

      verify(systemConfigRepo).deleteById(id1);
      verify(systemConfigRepo).deleteById(id2);
    }

    @Test
    @DisplayName("should call repository delete for non-existent id")
    void shouldCallRepositoryDeleteForNonExistentId() {
      var nonExistentId = UUID.randomUUID();

      service.deleteById(nonExistentId);

      verify(systemConfigRepo, times(1)).deleteById(nonExistentId);
    }
  }

  @Nested
  @DisplayName("findByReference()")
  class FindByReferenceTests {

    @Test
    @DisplayName("should return configuration when reference found")
    void shouldReturnConfigurationWhenReferenceFound() {
      when(systemConfigRepo.findByReference(reference)).thenReturn(Optional.of(testConfig));

      var result = service.findByReference(reference);

      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testConfig);
      assertThat(result.get().getReference()).isEqualTo(reference);
      verify(systemConfigRepo, times(1)).findByReference(reference);
    }

    @Test
    @DisplayName("should return empty optional when reference not found")
    void shouldReturnEmptyOptionalWhenReferenceNotFound() {
      when(systemConfigRepo.findByReference("NONEXISTENT_REF")).thenReturn(Optional.empty());

      var result = service.findByReference("NONEXISTENT_REF");

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findByReference("NONEXISTENT_REF");
    }

    @Test
    @DisplayName("should find configuration by exact reference match")
    void shouldFindConfigurationByExactReferenceMatch() {
      var customRef = "CUSTOM_REFERENCE";
      var customConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference(customRef)
          .value("custom_value")
          .build();

      when(systemConfigRepo.findByReference(customRef)).thenReturn(Optional.of(customConfig));

      var result = service.findByReference(customRef);

      assertThat(result).isPresent();
      assertThat(result.get().getReference()).isEqualTo(customRef);
      verify(systemConfigRepo).findByReference(customRef);
    }

    @Test
    @DisplayName("should be case sensitive for reference")
    void shouldBeCaseSensitiveForReference() {
      when(systemConfigRepo.findByReference("TEST_REFERENCE")).thenReturn(Optional.of(testConfig));
      when(systemConfigRepo.findByReference("test_reference")).thenReturn(Optional.empty());

      assertThat(service.findByReference("TEST_REFERENCE")).isPresent();
      assertThat(service.findByReference("test_reference")).isEmpty();
      verify(systemConfigRepo).findByReference("TEST_REFERENCE");
      verify(systemConfigRepo).findByReference("test_reference");
    }
  }

  @Nested
  @DisplayName("getValueByReference(String, String)")
  class GetValueByReferenceWithDefaultTests {

    @Test
    @DisplayName("should return value when configuration found and active")
    void shouldReturnValueWhenConfigurationFoundAndActive() {
      when(systemConfigRepo.findByReference(reference)).thenReturn(Optional.of(testConfig));

      var result = service.getValueByReference(reference, "default_value");

      assertThat(result).isEqualTo("test_value");
      verify(systemConfigRepo, times(1)).findByReference(reference);
    }

    @Test
    @DisplayName("should return default value when configuration not found")
    void shouldReturnDefaultValueWhenConfigurationNotFound() {
      var defaultValue = "default_value";
      when(systemConfigRepo.findByReference("NONEXISTENT")).thenReturn(Optional.empty());

      var result = service.getValueByReference("NONEXISTENT", defaultValue);

      assertThat(result).isEqualTo(defaultValue);
      verify(systemConfigRepo, times(1)).findByReference("NONEXISTENT");
    }

    @Test
    @DisplayName("should return default value when configuration found but inactive")
    void shouldReturnDefaultValueWhenConfigurationFoundButInactive() {
      var inactiveConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("INACTIVE_REF")
          .value("inactive_value")
          .active(false)
          .build();

      var defaultValue = "default_value";
      when(systemConfigRepo.findByReference("INACTIVE_REF")).thenReturn(Optional.of(inactiveConfig));

      var result = service.getValueByReference("INACTIVE_REF", defaultValue);

      assertThat(result).isEqualTo(defaultValue);
      verify(systemConfigRepo, times(1)).findByReference("INACTIVE_REF");
    }

    @Test
    @DisplayName("should return configuration value when active")
    void shouldReturnConfigurationValueWhenActive() {
      var customValue = "custom_configuration_value";
      var activeConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("CUSTOM_REF")
          .value(customValue)
          .active(true)
          .build();

      when(systemConfigRepo.findByReference("CUSTOM_REF")).thenReturn(Optional.of(activeConfig));

      var result = service.getValueByReference("CUSTOM_REF", "default");

      assertThat(result).isEqualTo(customValue);
      verify(systemConfigRepo).findByReference("CUSTOM_REF");
    }

    @Test
    @DisplayName("should handle null default value")
    void shouldHandleNullDefaultValue() {
      when(systemConfigRepo.findByReference("NONEXISTENT")).thenReturn(Optional.empty());

      var result = service.getValueByReference("NONEXISTENT", null);

      assertThat(result).isNull();
      verify(systemConfigRepo).findByReference("NONEXISTENT");
    }
  }

  @Nested
  @DisplayName("getValueByReference(String)")
  class GetValueByReferenceTests {

    @Test
    @DisplayName("should return value when configuration found and active")
    void shouldReturnValueWhenConfigurationFoundAndActive() {
      when(systemConfigRepo.findByReference(reference)).thenReturn(Optional.of(testConfig));

      var result = service.getValueByReference(reference);

      assertThat(result).isEqualTo("test_value");
      verify(systemConfigRepo, times(1)).findByReference(reference);
    }

    @Test
    @DisplayName("should return empty string when configuration not found")
    void shouldReturnEmptyStringWhenConfigurationNotFound() {
      when(systemConfigRepo.findByReference("NONEXISTENT")).thenReturn(Optional.empty());

      var result = service.getValueByReference("NONEXISTENT");

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findByReference("NONEXISTENT");
    }

    @Test
    @DisplayName("should return empty string when configuration found but inactive")
    void shouldReturnEmptyStringWhenConfigurationFoundButInactive() {
      var inactiveConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("INACTIVE_REF")
          .value("inactive_value")
          .active(false)
          .build();

      when(systemConfigRepo.findByReference("INACTIVE_REF")).thenReturn(Optional.of(inactiveConfig));

      var result = service.getValueByReference("INACTIVE_REF");

      assertThat(result).isEmpty();
      verify(systemConfigRepo, times(1)).findByReference("INACTIVE_REF");
    }

    @Test
    @DisplayName("should return configuration value when active")
    void shouldReturnConfigurationValueWhenActive() {
      var customValue = "active_configuration_value";
      var activeConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("CUSTOM_REF")
          .value(customValue)
          .active(true)
          .build();

      when(systemConfigRepo.findByReference("CUSTOM_REF")).thenReturn(Optional.of(activeConfig));

      var result = service.getValueByReference("CUSTOM_REF");

      assertThat(result).isEqualTo(customValue);
      verify(systemConfigRepo).findByReference("CUSTOM_REF");
    }

    @Test
    @DisplayName("should filter by active status correctly")
    void shouldFilterByActiveStatusCorrectly() {
      var activeConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("FILTER_REF")
          .value("active_value")
          .active(true)
          .build();

      var inactiveConfig = SystemConfiguration.builder()
          .id(UUID.randomUUID())
          .group(testGroup)
          .reference("FILTER_REF_2")
          .value("inactive_value")
          .active(false)
          .build();

      when(systemConfigRepo.findByReference("FILTER_REF")).thenReturn(Optional.of(activeConfig));
      when(systemConfigRepo.findByReference("FILTER_REF_2")).thenReturn(Optional.of(inactiveConfig));

      assertThat(service.getValueByReference("FILTER_REF")).isEqualTo("active_value");
      assertThat(service.getValueByReference("FILTER_REF_2")).isEmpty();
      verify(systemConfigRepo).findByReference("FILTER_REF");
      verify(systemConfigRepo).findByReference("FILTER_REF_2");
    }
  }
}
