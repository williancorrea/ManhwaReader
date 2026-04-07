package dev.williancorrea.manhwa.reader.features.work;

import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class WorkResource {

  private final WorkService workService;

  @Value("${minio.url.public}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<WorkCatalogOutput>> findAllWorks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) WorkType type,
      @RequestParam(required = false) WorkPublicationDemographic publicationDemographic,
      @RequestParam(required = false) WorkStatus status,
      @RequestParam(required = false) String sort
  ) {
    size = Math.min(size, 50);
    var filter = new WorkCatalogFilter(title, type, publicationDemographic, status, sort);
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        workService.findAllWorks(filter, pageable)
            .map(work -> WorkCatalogOutput.fromEntity(work, minioUrl + "/" + bucketName))
    );
  }
}
