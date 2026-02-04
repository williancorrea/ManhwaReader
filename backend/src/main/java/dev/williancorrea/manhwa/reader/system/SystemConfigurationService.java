package dev.williancorrea.manhwa.reader.system;

import java.util.List;
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
}