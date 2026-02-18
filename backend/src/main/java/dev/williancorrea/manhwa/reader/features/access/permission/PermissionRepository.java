package dev.williancorrea.manhwa.reader.features.access.permission;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
