package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.scraper.mangadex.MangaDexApiService;
import dev.williancorrea.manhwa.reader.scraper.mangadex.client.MangaDexClient;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexResponseList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AdminSynchronizationService {

  private final WorkService workService;
  private final MangaDexApiService mangaDexApiService;
  private final MangaDexClient mangaDexClient;

  @Value("${minio.url.public}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  public AdminSynchronizationService(
      @Lazy WorkService workService,
      @Lazy MangaDexApiService mangaDexApiService,
      @Lazy MangaDexClient mangaDexClient) {
    this.workService = workService;
    this.mangaDexApiService = mangaDexApiService;
    this.mangaDexClient = mangaDexClient;
  }

  @Transactional(readOnly = true)
  public Page<AdminWorkOutput> listWorks(String title, Pageable pageable) {
    var filter = new WorkCatalogFilter(title, null, null, null, null);
    var storageBase = minioUrl + "/" + bucketName;
    Page<Work> worksPage = workService.findAllWorks(filter, pageable);
    return worksPage.map(work -> AdminWorkOutput.fromEntity(work, storageBase));
  }

  public List<MangaDexSearchOutput> searchMangaDex(String title, int page, int size) {
    MangaDexResponseList response = mangaDexClient.searchManga(
        title, size, page * size,
        List.of("artist", "author", "cover_art")
    );

    if (!"ok".equals(response.getResult()) || response.getData() == null) {
      return List.of();
    }

    return response.getData().stream()
        .map(MangaDexSearchOutput::fromMangaDexData)
        .toList();
  }

  @Transactional
  public void linkWorkToMangaDex(UUID workId, String mangaDexId) {
    Work work = workService.findById(workId)
        .orElseThrow(() -> new BusinessException("work.not-found", null));

    if (work.hasSynchronizationOrigin(SynchronizationOriginType.MANGADEX)) {
      throw new ConflictException("work.already-linked-to-mangadex", null);
    }

    if (work.getSynchronizations() == null) {
      work.setSynchronizations(new ArrayList<>());
    }

    work.getSynchronizations().add(WorkSynchronization.builder()
        .work(work)
        .origin(SynchronizationOriginType.MANGADEX)
        .externalId(mangaDexId)
        .build());
    workService.save(work);

    log.info("Work '{}' linked to MangaDex ID '{}'", work.getSlug(), mangaDexId);

    mangaDexApiService.searchMangaByIDFromExternalApi(mangaDexId);
  }
}
