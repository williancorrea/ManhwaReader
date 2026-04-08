package dev.williancorrea.manhwa.reader.features.chapter;

import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterListOutput;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgress;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgressService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class ChapterResource {

  private final ChapterService chapterService;
  private final ReadingProgressService readingProgressService;
  private final UserRepository userRepository;

  @GetMapping("/{slug}/chapters")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<ChapterListOutput>> findChapters(
      @PathVariable String slug,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam(defaultValue = "desc") String sort,
      @RequestParam(required = false) String language,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    size = Math.min(size, 100);
    Page<Chapter> chapters = chapterService.findPagedByWorkSlug(slug, page, size, sort, language);

    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    List<UUID> chapterIds = chapters.getContent().stream().map(Chapter::getId).toList();
    Map<UUID, ReadingProgress> progressMap = readingProgressService.findAllByUserAndChapterIds(user, chapterIds);

    return ResponseEntity.ok(chapters.map(chapter ->
        ChapterListOutput.fromEntity(chapter, progressMap.get(chapter.getId()))
    ));
  }

  @PostMapping("/{slug}/chapters/{chapterId}/read")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> markChapterRead(
      @PathVariable String slug,
      @PathVariable UUID chapterId,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var chapter = chapterService.findById(chapterId).orElseThrow();
    readingProgressService.saveOrUpdate(user, chapter);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{slug}/chapters/{chapterId}/read")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> markChapterUnread(
      @PathVariable String slug,
      @PathVariable UUID chapterId,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var chapter = chapterService.findById(chapterId).orElseThrow();
    readingProgressService.deleteByUserAndChapter(user, chapter);
    return ResponseEntity.noContent().build();
  }
}
