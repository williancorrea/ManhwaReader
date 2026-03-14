package dev.williancorrea.manhwa.reader.scraper.base.input;

import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SynchronizationTitle {

  private String language;

  @NotEmpty
  private SynchronizationOriginType origin;

  @NotNull
  @NotEmpty
  private String title;
}
