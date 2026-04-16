package dev.williancorrea.manhwa.reader.features.work;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.library.LibraryService;
import dev.williancorrea.manhwa.reader.features.library.LibraryStatus;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgressService;
import dev.williancorrea.manhwa.reader.features.rating.RatingService;
import dev.williancorrea.manhwa.reader.features.work.dto.LibraryInput;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogFilter;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkCatalogOutput;
import dev.williancorrea.manhwa.reader.features.work.dto.WorkDetailOutput;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class WorkResource {

  private final WorkService workService;
  private final LibraryService libraryService;
  private final RatingService ratingService;
  private final ReadingProgressService readingProgressService;
  private final UserRepository userRepository;

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
      @RequestParam(required = false) String sort,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    size = Math.min(size, 50);
    var filter = new WorkCatalogFilter(title, type, publicationDemographic, status, sort);
    var storageBase = minioUrl + "/" + bucketName;

    Page<Work> worksPage = workService.findAllWorks(filter, PageRequest.of(page, size));

    User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    List<UUID> workIds = worksPage.map(Work::getId).getContent();
    Map<UUID, LibraryStatus> libraryMap = libraryService.findStatusMapByUserAndWorkIds(user, workIds);

    return ResponseEntity.ok(
        worksPage.map(work -> WorkCatalogOutput.fromEntity(
            work,
            storageBase,
            libraryMap.containsKey(work.getId()) ? libraryMap.get(work.getId()).name() : null
        ))
    );
  }

  @GetMapping("/{slug}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<WorkDetailOutput> findBySlug(
      @PathVariable String slug,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var work = workService.findBySlugWithDetails(slug).orElseThrow();
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var library = libraryService.findByUserAndWork(user, work);
    var rating = ratingService.findByUserAndWork(user, work);
    return ResponseEntity.ok(WorkDetailOutput.fromEntity(work, minioUrl + "/" + bucketName, library, rating));
  }

  @PostMapping("/{slug}/library")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> addToLibrary(
      @PathVariable String slug,
      @RequestBody LibraryInput input,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var work = workService.findBySlug(slug).orElseThrow();
    User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    libraryService.saveOrUpdate(user, work, LibraryStatus.valueOf(input.status()));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{slug}/library")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> removeFromLibrary(
      @PathVariable String slug,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var work = workService.findBySlug(slug).orElseThrow();
    User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    libraryService.deleteByUserAndWork(user, work);
    readingProgressService.unmarkAllByWorkId(user, work.getId());
    return ResponseEntity.noContent().build();
  }
}
