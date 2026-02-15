package dev.williancorrea.manhwa.reader.system;

import dev.williancorrea.manhwa.reader.system.SystemConfigurationGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationGroupRepository extends JpaRepository<SystemConfigurationGroup, UUID> {
}
