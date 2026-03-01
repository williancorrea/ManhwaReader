package dev.williancorrea.manhwa.reader.features.volume;

import java.util.Objects;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class VolumeService {

  private final VolumeRepository repository;

  public VolumeService(@Lazy VolumeRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Volume findOrCreate(Work work, String title, Integer number) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(title);
    Objects.requireNonNull(StringUtils.isBlank(title) ? null : title.trim());

    return repository.findByWorkAndTitle(work.getId(), title)
        .orElseGet(() -> save(
                Volume.builder()
                    .work(work)
                    .number(number)
                    .title(title)
                    .build()
            )
        );
  }

  public Volume save(Volume entity) {
    return repository.save(entity);
  }
}