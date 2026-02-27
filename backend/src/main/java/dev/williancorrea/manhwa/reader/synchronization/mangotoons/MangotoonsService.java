package dev.williancorrea.manhwa.reader.synchronization.mangotoons;

import java.util.Objects;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MangotoonsService {
  public final WorkService workService;

  private static final String CDN_MANOTOONS = "https://cdn.mangotoons.com/obras";

  public void start(Work work) {
    Objects.requireNonNull(work);

    log.info("--> Starting Mangotoons synchronization for work: {}", work.getTitles().getFirst().getTitle());
    String title = work.getTitles().getFirst().getTitle();

//    https://cdn.mangotoons.com/obras/10648/capitulo-1/pagina_001.jpg
    log.info("<-- Lycantoons synchronization completed for work: {}", work.getTitles().getFirst().getTitle());
  }
}
