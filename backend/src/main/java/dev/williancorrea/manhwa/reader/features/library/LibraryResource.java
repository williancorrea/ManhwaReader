package dev.williancorrea.manhwa.reader.features.library;

import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.library.dto.LibraryItemOutput;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
public class LibraryResource {

  private final LibraryService libraryService;
  private final UserRepository userRepository;

  @Value("${minio.url.public}")
  private String minioUrl;

  @Value("${minio.bucket.name}")
  private String bucketName;

  @GetMapping("/continue-reading")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<LibraryItemOutput>> getContinueReading(
      @RequestParam(defaultValue = "6") int size,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    size = Math.min(size, 20);
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    return ResponseEntity.ok(
        libraryService.findContinueReading(user.getId(), size, minioUrl + "/" + bucketName)
    );
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<LibraryItemOutput>> getUserLibrary(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) LibraryStatus status,
      @RequestParam(required = false) String title,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    size = Math.min(size, 50);
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        libraryService.findUserLibrary(user.getId(), status, title, pageable, minioUrl + "/" + bucketName)
    );
  }
}
