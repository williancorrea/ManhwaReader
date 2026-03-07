package dev.williancorrea.manhwa.reader.synchronization.base.input;

import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SynchronizationTags {

  @NotNull
  private TagGroupType group;
  
  @NotNull
  @NotEmpty
  private String name;
}
