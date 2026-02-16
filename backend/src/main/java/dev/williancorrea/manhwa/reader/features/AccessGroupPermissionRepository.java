package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessGroupPermissionRepository extends JpaRepository<AccessGroupPermission, AccessGroupPermissionId> {
  List<AccessGroupPermission> findAllByAccessGroup_Id(UUID accessGroupId);
  List<AccessGroupPermission> findAllByPermission_Id(UUID permissionId);
}
