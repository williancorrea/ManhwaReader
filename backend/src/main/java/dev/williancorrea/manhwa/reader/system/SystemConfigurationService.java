package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class SystemConfigurationService {

  private final SystemConfigurationRepository systemConfigRepo;

  public SystemConfigurationService(@Lazy SystemConfigurationRepository systemConfigRepo) {
    this.systemConfigRepo = systemConfigRepo;
  }

  public List<SystemConfiguration> findAll() {
    return systemConfigRepo.findAll();
  }
  
  public List<SystemConfiguration> findAllByGroup(SystemConfigurationGroup group) {
    return systemConfigRepo.findAllByGroup(group);
  }

  public Optional<SystemConfiguration> findById(UUID id) {
    return systemConfigRepo.findById(id);
  }

  public SystemConfiguration save(SystemConfiguration entity) {
    return systemConfigRepo.save(entity);
  }

  public boolean existsById(UUID id) {
    return systemConfigRepo.existsById(id);
  }

  public void deleteById(UUID id) {
    systemConfigRepo.deleteById(id);
  }
}
