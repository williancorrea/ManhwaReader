package dev.williancorrea.manhwa.reader.features.tag;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, UUID> {

  @Query(
      nativeQuery = true,
      value = "select t.* from tag t where t.group_tag = :group and (t.name = :name or t.alias1 = :name or t.alias2 = :name or t.alias3 = :name)"
  )
  Optional<Tag> findByGroupAndName(String group, String name);
}
