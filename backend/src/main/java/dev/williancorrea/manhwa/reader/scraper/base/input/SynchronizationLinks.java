package dev.williancorrea.manhwa.reader.scraper.base.input;

import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SynchronizationLinks {

  private String link;

  @NotEmpty
  private SiteType siteType;
}
