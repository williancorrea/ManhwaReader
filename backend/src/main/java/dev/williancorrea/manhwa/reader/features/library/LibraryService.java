package dev.williancorrea.manhwa.reader.features.library;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.library.dto.LibraryItemOutput;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class LibraryService {

  private final LibraryRepository repository;

  public LibraryService(@Lazy LibraryRepository repository) {
    this.repository = repository;
  }

  public List<Library> findAll() {
    return repository.findAll();
  }

  public Optional<Library> findById(UUID id) {
    return repository.findById(id);
  }

  public Library save(Library entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  public Map<UUID, LibraryStatus> findStatusMapByUserAndWorkIds(User user, List<UUID> workIds) {
    if (workIds == null || workIds.isEmpty()) return Map.of();
    return repository.findByUserIdAndWorkIdIn(user.getId(), workIds)
        .stream()
        .collect(Collectors.toMap(
            l -> l.getWork().getId(),
            Library::getStatus
        ));
  }

  public Optional<Library> findByUserAndWork(User user, Work work) {
    return repository.findByUser_IdAndWork_Id(user.getId(), work.getId());
  }

  @Transactional
  public Library saveOrUpdate(User user, Work work, LibraryStatus status) {
    Library library = repository.findByUser_IdAndWork_Id(user.getId(), work.getId())
        .orElse(Library.builder().user(user).work(work).build());
    library.setStatus(status);
    return repository.save(library);
  }

  @Transactional
  public void deleteByUserAndWork(User user, Work work) {
    repository.deleteByUserIdAndWorkId(user.getId(), work.getId());
  }

  @Transactional(readOnly = true)
  public Page<LibraryItemOutput> findUserLibrary(UUID userId, LibraryStatus status, Pageable pageable, String storageBaseUrl) {
    String statusStr = status != null ? status.name() : null;
    Page<Object[]> page = repository.findLibraryItemsByUserId(userId, statusStr, pageable);

    return page.map(row -> {
      String workId = row[1] != null ? row[1].toString() : null;
      String slug = row[2] != null ? row[2].toString() : null;
      String libraryStatus = row[3] != null ? row[3].toString() : null;
      String publicationDemographic = row[4] != null ? row[4].toString() : null;
      String workStatus = row[5] != null ? row[5].toString() : null;
      Long chapterCount = row[6] != null ? ((Number) row[6]).longValue() : 0L;
      String title = row[7] != null ? row[7].toString() : null;
      String coverFileName = row[8] != null ? row[8].toString() : null;
      Long unreadCount = row[9] != null ? ((Number) row[9]).longValue() : 0L;

      String coverUrl = null;
      if (slug != null && coverFileName != null) {
        String basePath = publicationDemographic != null ? publicationDemographic.toLowerCase() : "unknown";
        coverUrl = storageBaseUrl + "/" + basePath + "/" + slug + "/covers/" + coverFileName;
      }

      return new LibraryItemOutput(
          workId != null ? UUID.fromString(workId) : null,
          slug,
          title,
          coverUrl,
          publicationDemographic,
          workStatus,
          chapterCount,
          libraryStatus,
          unreadCount
      );
    });
  }
}

