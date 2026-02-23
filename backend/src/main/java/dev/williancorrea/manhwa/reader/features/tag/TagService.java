package dev.williancorrea.manhwa.reader.features.tag;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class TagService {

  private final TagRepository repository;

  public TagService(@Lazy TagRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Tag save(Tag tag) {
    return repository.saveAndFlush(tag);
  }

  @Transactional
  public Tag findOrCreate(TagGroupType group, String name) {
    return repository.findByGroupAndName(group.name(), name)
        .orElseGet(() -> save(
                Tag.builder()
                    .group(group)
                    .name(name)
                    .build()
            )
        );

  }
}

