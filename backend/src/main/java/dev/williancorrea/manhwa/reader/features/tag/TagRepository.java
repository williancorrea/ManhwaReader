package dev.williancorrea.manhwa.reader.features.tag;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, UUID> {

  @Query(
      nativeQuery = true,
      value = "select t.* from tag t where lower(t.group_tag) = lower(:group) and (lower(t.name) = lower(:name) or lower(t.alias1) = lower(:name) or lower(t.alias2) = lower(:name) or lower(t.alias3) = lower(:name))"
  )
  Optional<Tag> findByGroupAndName(String group, String name);
}
