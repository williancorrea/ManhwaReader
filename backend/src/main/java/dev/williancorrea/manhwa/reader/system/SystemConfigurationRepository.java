package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, UUID> {

  List<SystemConfiguration> findAllByGroup(SystemConfigurationGroup type);
}