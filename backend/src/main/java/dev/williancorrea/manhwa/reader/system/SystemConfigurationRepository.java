package dev.williancorrea.manhwa.reader.system;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, UUID> {

  @Query("SELECT s FROM SystemConfiguration s WHERE s.group.name = :name")
  List<SystemConfiguration> findByGroupName(String name);

  List<SystemConfiguration> findAllByGroup(SystemConfigurationGroup type);

  Optional<SystemConfiguration> findByReference(String reference);
}