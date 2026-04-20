package dev.williancorrea.manhwa.reader.features.chapter;

import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterListOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterNavOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterPageOutput;
import dev.williancorrea.manhwa.reader.features.chapter.dto.ChapterReaderOutput;
import dev.williancorrea.manhwa.reader.features.library.LibraryService;
import dev.williancorrea.manhwa.reader.features.library.LibraryStatus;
import dev.williancorrea.manhwa.reader.features.page.PageService;
import dev.williancorrea.manhwa.reader.features.page.PageType;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgress;
import dev.williancorrea.manhwa.reader.features.progress.ReadingProgressService;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.storage.StorageInterface;
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
  private final LibraryService libraryService;
  private final UserRepository userRepository;
  private final WorkService workService;
  private final PageService pageService;
  private final StorageInterface storageService;

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
    var work = chapter.getWork();
    // Add to library as READING if not already in library
    if (libraryService.findByUserAndWork(user, work).isEmpty()) {
      libraryService.saveOrUpdate(user, work, LibraryStatus.READING);
    }
    // Mark this chapter and all previous chapters as read
    List<Chapter> chaptersToMark = chapterService.findChaptersUpTo(
        work.getId(), chapter.getNumberFormatted(), chapter.getNumberVersion());
    readingProgressService.markAllAsRead(user, chaptersToMark);
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
    // Unmark this chapter and all subsequent chapters
    List<Chapter> chaptersToUnmark = chapterService.findChaptersFrom(
        chapter.getWork().getId(), chapter.getNumberFormatted(), chapter.getNumberVersion());
    for (Chapter c : chaptersToUnmark) {
      readingProgressService.deleteByUserAndChapter(user, c);
    }
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{slug}/chapters/read-all")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> markAllChaptersRead(
      @PathVariable String slug,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var work = workService.findBySlug(slug).orElseThrow();
    List<Chapter> chapters = chapterService.findAllByWorkId(work.getId());
    readingProgressService.markAllAsRead(user, chapters);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{slug}/chapters/read-all")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> markAllChaptersUnread(
      @PathVariable String slug,
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    var work = workService.findBySlug(slug).orElseThrow();
    readingProgressService.unmarkAllByWorkId(user, work.getId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{slug}/chapters/{chapterId}/reader")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ChapterReaderOutput> getChapterForReader(
      @PathVariable String slug,
      @PathVariable UUID chapterId
  ) {
    var chapter = chapterService.findById(chapterId).orElseThrow();
    var work = chapter.getWork();

    if (!work.getSlug().equals(slug)) {
      return ResponseEntity.notFound().build();
    }

    // Get work title
    String workTitle = null;
    if (work.getTitles() != null && !work.getTitles().isEmpty()) {
      workTitle = work.getTitles().stream()
          .filter(t -> Boolean.TRUE.equals(t.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findFirst()
          .orElse(work.getTitles().getFirst().getTitle());
    }

    // Build page list with presigned URLs
    var pages = pageService.findAllByChapterIdNotDisabled(chapterId);
    var pageOutputs = pages.stream().map(page -> {
      String imageUrl = null;
      String content = null;

      if (page.getType() == PageType.IMAGE) {
        var path = work.getPublicationDemographic().name().toLowerCase()
            + "/" + work.getSlug()
            + "/chapters/" + chapter.getNumberFormatted()
            + "/" + chapter.getScanlator().getCode().toLowerCase()
            + "/" + chapter.getLanguage().getCode().toLowerCase()
            + "/" + chapter.getNumberVersion()
            + "/" + page.getFileName();
        imageUrl = storageService.findObjectByNamePresigned(path.toLowerCase());
      } else {
        content = page.getContent();
      }

      return new ChapterPageOutput(
          page.getPageNumber(),
          page.getType().name(),
          imageUrl,
          content
      );
    }).toList();

    // Find previous and next chapters
    ChapterNavOutput previousChapter = chapterService.findPreviousChapter(chapter)
        .map(c -> new ChapterNavOutput(c.getId(), c.getNumberWithVersionInteger()))
        .orElse(null);

    ChapterNavOutput nextChapter = chapterService.findNextChapter(chapter)
        .map(c -> new ChapterNavOutput(c.getId(), c.getNumberWithVersionInteger()))
        .orElse(null);

    return ResponseEntity.ok(new ChapterReaderOutput(
        chapter.getId(),
        chapter.getNumber(),
        chapter.getNumberWithVersionInteger(),
        chapter.getTitle(),
        workTitle,
        work.getSlug(),
        work.getType() != null ? work.getType().name() : null,
        pageOutputs,
        previousChapter,
        nextChapter
    ));
  }
}
