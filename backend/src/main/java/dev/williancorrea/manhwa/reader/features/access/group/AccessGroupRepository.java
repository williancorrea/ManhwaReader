package dev.williancorrea.manhwa.reader.features.access.group;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessGroupRepository extends JpaRepository<AccessGroup, UUID> {
}
