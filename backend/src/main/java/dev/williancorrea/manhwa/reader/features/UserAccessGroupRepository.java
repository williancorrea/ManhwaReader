package dev.williancorrea.manhwa.reader.features;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessGroupRepository extends JpaRepository<UserAccessGroup, UserAccessGroupId> {
  List<UserAccessGroup> findAllByUser_Id(UUID userId);
  List<UserAccessGroup> findAllByAccessGroup_Id(UUID accessGroupId);
}
