package dev.williancorrea.manhwa.reader.scraper.base;

import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_AUTHOR_LIST_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_CREATE_AT_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_BLANK;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_NAME_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_MESSAGE_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_SITE_LINK_IS_BLANK;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_SYNOPSES_LIST_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_TAG_LIST_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_TITLE_IS_BLANK;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_TITLE_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_TITLE_LIST_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_UPDATE_AT_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_PUBLICATION_DEMOGRAPHIC_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_SLUG_IS_BLANK;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_SLUG_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_TYPE_IS_NULL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorService;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.scanlator.ScanlatorService;
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationError;
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationErrorService;
import dev.williancorrea.manhwa.reader.features.tag.TagService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkAuthor;
import dev.williancorrea.manhwa.reader.features.work.WorkContentRating;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.WorkTag;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.cover.WorkCover;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLinkRepository;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.storage.ExternalFileService;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationLinks;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTags;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.utils.RemoveAccentuationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScraperBase {
  public final WorkService workService;
  public final LanguageService languageService;
  public final ScanlatorSynchronizationErrorService scanlatorSynchronizationErrorService;
  public final ScanlatorService scanlatorService;
  public final WorkLinkRepository workLinkRepository;
  public final TagService tagService;
  public final AuthorService authorService;
  public final ExternalFileService externalFileService;
  public final dev.williancorrea.manhwa.reader.email.EmailService emailService;

  public ScraperBase(@Lazy WorkService workService,
                     @Lazy LanguageService languageService,
                     @Lazy ScanlatorSynchronizationErrorService scanlatorSynchronizationErrorService,
                     @Lazy ScanlatorService scanlatorService,
                     @Lazy WorkLinkRepository workLinkRepository,
                     @Lazy TagService tagService,
                     @Lazy AuthorService authorService,
                     @Lazy ExternalFileService externalFileService,
                     @Lazy dev.williancorrea.manhwa.reader.email.EmailService emailService) {
    this.workService = workService;
    this.languageService = languageService;
    this.scanlatorSynchronizationErrorService = scanlatorSynchronizationErrorService;
    this.scanlatorService = scanlatorService;
    this.workLinkRepository = workLinkRepository;
    this.tagService = tagService;
    this.authorService = authorService;
    this.externalFileService = externalFileService;
    this.emailService = emailService;
  }

  public void sleep(long millis) {
    try {
      log.debug("--> [SynchronizationBase][sleep] Sleeping for ({}) seconds", millis);
      Thread.sleep(millis);
    } catch (InterruptedException _) {
      log.error("<-- [SynchronizationBase][sleep] Error sleeping for ({}) seconds", millis);
      Thread.currentThread().interrupt();
    }
  }

  public Work findWorkOrCreate(String externalID,
                               SynchronizationOriginType origin) {
    log.debug("--> [SynchronizationBase][findWorkOrCreate] Finding work with externalID: ({}) and origin ({})",
        externalID, origin);

    Objects.requireNonNull(externalID);
    Objects.requireNonNull(origin);

    var work = workService.findBySynchronizationExternalID(externalID, origin).orElse(null);

    if (work == null) {
      log.debug(
          "--> [SynchronizationBase][findWorkOrCreate] Work not found, creating new work with externalID: ({}) and origin ({})",
          externalID, origin);
      work = Work.builder()
          .createdAt(OffsetDateTime.now())
          .disabled(false)
          .build();
    }
    return work;
  }

  public void syncTitle(Work work,
                        List<SynchronizationTitle> titles) {
    log.debug("--> [SynchronizationBase][syncTitle] Syncing title");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(titles, VALIDATION_ERROR_TITLE_LIST_IS_NULL);

    if (work.getTitles() == null) {
      work.setTitles(new ArrayList<>());
    }

    titles.forEach(item -> {
      Objects.requireNonNull(item.getOrigin(), VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
      Objects.requireNonNull(item.getTitle(), VALIDATION_ERROR_TITLE_IS_NULL);

      if (item.getTitle().isBlank()) {
        throw new IllegalArgumentException(VALIDATION_ERROR_TITLE_IS_BLANK);
      }

      var lang = item.getLanguage() == null
          ? null
          : languageService.findOrCreate(item.getLanguage(), item.getOrigin());

      AtomicBoolean found = new AtomicBoolean(false);
      log.debug("--> [SynchronizationBase][syncTitle] ({}) Syncing title", item.getTitle());
      work.getTitles().forEach(obj -> {
        if (obj.getTitle().equalsIgnoreCase(item.getTitle()) && obj.getLanguage() == null) {
          obj.setLanguage(lang);
          found.set(true);
        } else if (obj.getTitle().equalsIgnoreCase(item.getTitle())
            && obj.getLanguage().getCode().equalsIgnoreCase(item.getLanguage())) {
          found.set(true);
        }
      });
      if (!found.get()) {
        work.getTitles().add(WorkTitle.builder()
            .title(item.getTitle())
            .language(lang)
            .isOfficial(false)
            .work(work)
            .build());
      }
    });
  }

  public void syncSynopses(Work work,
                           List<SynchronizationSynopses> synopses) {
    log.debug("--> [SynchronizationBase][syncSynopses] Syncing synopses");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(synopses, VALIDATION_ERROR_SYNOPSES_LIST_IS_NULL);

    if (work.getSynopses() == null) {
      work.setSynopses(new ArrayList<>());
    }

    synopses.forEach(synopsis -> {
      if (synopsis.getDescription().isBlank()) {
        log.debug("--> [SynchronizationBase][syncSynopses] Skipping synopsis because is empty");
        return;
      }

      AtomicBoolean found = new AtomicBoolean(false);
      work.getSynopses().forEach(obj -> {
        if (obj.getLanguage().getCode().equalsIgnoreCase(synopsis.getLanguage())) {
          found.set(true);
        }
      });
      if (!found.get()) {
        work.getSynopses().add(
            WorkSynopsis.builder()
                .work(work)
                .language(languageService.findOrCreate(synopsis.getLanguage(), synopsis.getOrigin()))
                .description(synopsis.getDescription())
                .build()
        );
      }
    });

  }

  public void syncWorkError(SynchronizationOriginType scan,
                            String workId,
                            String externalWorkId,
                            String externalWorkName,
                            String errorMessage,
                            String stackTrace) {
    syncWorkError(scan, workId, externalWorkId, externalWorkName, errorMessage, stackTrace, null);
  }

  public void syncWorkError(SynchronizationOriginType scan,
                            String workId,
                            String externalWorkId,
                            String externalWorkName,
                            String errorMessage,
                            String stackTrace,
                            String url) {
    log.error("--> [SynchronizationBase][syncWorkError] Error syncing work");

    Objects.requireNonNull(scan, VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
    Objects.requireNonNull(externalWorkId, VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL);
    Objects.requireNonNull(externalWorkName, VALIDATION_ERROR_EXTERNAL_WORK_NAME_IS_NULL);
    Objects.requireNonNull(errorMessage, VALIDATION_ERROR_MESSAGE_IS_NULL);

    scanlatorSynchronizationErrorService.save(
        ScanlatorSynchronizationError.builder()
            .scanlator(scanlatorService.findBySynchronization(scan)
                .orElseGet(() -> null))
            .workId(workId)
            .externalWorkId(externalWorkId)
            .externalWorkName(externalWorkName)
            .errorMessage(errorMessage)
            .stackTrace(stackTrace)
            .build()
    );

    // Send error notification email
    try {
      var scanlator = scanlatorService.findBySynchronization(scan)
          .map(s -> s.getName())
          .orElse(scan.name());

      var errorDetails = new java.util.HashMap<String, Object>();
      errorDetails.put("workTitle", externalWorkName);
      errorDetails.put("errorTime", java.time.LocalDateTime.now().toString());
      errorDetails.put("errorType", "SynchronizationError");
      if (stackTrace != null) {
        errorDetails.put("stackTrace", stackTrace);
      }
      if (url != null) {
        errorDetails.put("url", url);
      }

      emailService.sendScraperErrorEmail(scanlator, errorMessage, errorDetails);
    } catch (Exception e) {
      log.error("Failed to send error notification email", e);
    }
  }

  @SuppressWarnings({"java:S3776", "java:S107"})
  public void syncAttributes(Work work,
                             WorkPublicationDemographic publicationDemographic,
                             WorkType workType,
                             WorkStatus workStatus,
                             String workSlug,
                             Boolean isNovel,
                             Integer releaseYear,
                             Language originalLanguage,
                             WorkContentRating contentRating,
                             Boolean chapterNumbersResetOnNewVolume) {
    log.debug("--> [SynchronizationBase][syncAttributes] Syncing attributes");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(publicationDemographic, VALIDATION_ERROR_WORK_PUBLICATION_DEMOGRAPHIC_IS_NULL);
    Objects.requireNonNull(workType, VALIDATION_ERROR_WORK_TYPE_IS_NULL);
    Objects.requireNonNull(workStatus, VALIDATION_ERROR_WORK_TYPE_IS_NULL);
    Objects.requireNonNull(workSlug, VALIDATION_ERROR_WORK_SLUG_IS_NULL);

    if (workSlug.isBlank()) {
      throw new IllegalArgumentException(VALIDATION_ERROR_WORK_SLUG_IS_BLANK);
    }

    if (work.getType() == null) {
      work.setType(workType);
    }

    if (work.getPublicationDemographic() == null
        || work.getPublicationDemographic().equals(WorkPublicationDemographic.UNKNOWN)) {
      work.setPublicationDemographic(publicationDemographic);
    }

    if (originalLanguage != null && work.getOriginalLanguage() == null) {
      work.setOriginalLanguage(originalLanguage);

      if (!work.getType().equals(WorkType.NOVEL)) {
        var code = work.getOriginalLanguage().getCode();
        if (code.startsWith("ja")) {
          work.setType(WorkType.MANGA);
        } else if (code.startsWith("ko")) {
          work.setType(WorkType.MANHWA);
        } else if (code.startsWith("zh")) {
          work.setType(WorkType.MANHUA);
        }
      }
    }

    if (work.getStatus() == null) {
      work.setStatus(workStatus);
    }

    if (work.getSlug() == null || work.getSlug().isEmpty()) {
      work.setSlug(
          getWorkSlug(workSlug, isNovel)
      );
    }

    if (releaseYear != null && work.getReleaseYear() == null) {
      work.setReleaseYear(releaseYear);
    }

    if (contentRating != null && work.getContentRating() == null) {
      work.setContentRating(contentRating);
    }

    if (chapterNumbersResetOnNewVolume != null && work.getChapterNumbersResetOnNewVolume() == null) {
      work.setChapterNumbersResetOnNewVolume(chapterNumbersResetOnNewVolume);
    }
  }

  private String getWorkSlug(String name,
                             Boolean isNovel) {
    log.debug("--> [SynchronizationBase][getWorkSlug] ({}) Generating slug", name);

    var workSlug = RemoveAccentuationUtils.normalize(name).toLowerCase();
    if (validateSlugUniqueFree(workSlug, isNovel)) {
      return workSlug;
    } else {
      var random = workSlug + "__GENERATED_" + UUID.randomUUID();
      return Boolean.TRUE.equals(isNovel) ? random + "__novel" : random;
    }
  }

  private boolean validateSlugUniqueFree(String slug,
                                         Boolean isNovel) {
    if (slug == null || slug.isEmpty()) {
      return false;
    }
    return workService.findBySlug(Boolean.TRUE.equals(isNovel) ? slug + "__novel" : slug).isEmpty();
  }

  public void syncSynchronization(Work work,
                                  String externalId,
                                  SynchronizationOriginType origem,
                                  String externalSlug) {
    log.debug("--> [SynchronizationBase][syncSynchronization] Syncing synchronization");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(externalId, VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL);
    Objects.requireNonNull(origem, VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);

    if (work.getSynchronizations() == null) {
      work.setSynchronizations(new ArrayList<>());
    }

    if (!work.hasSynchronizationOrigin(origem)) {
      work.getSynchronizations().add(
          WorkSynchronization.builder()
              .externalId(externalId)
              .origin(origem)
              .work(work)
              .externalSlug(externalSlug)
              .build());
    }
  }

  public boolean isWorkUpdated(Work work, SynchronizationOriginType origem, OffsetDateTime updateAt) {
    log.debug("--> [SynchronizationBase][isWorkUpdated] Checking if work is updated");
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(origem, VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
    Objects.requireNonNull(updateAt, VALIDATION_ERROR_UPDATE_AT_IS_NULL);

    if (work.getSynchronizations() == null) {
      return false;
    }

    var scanlator = scanlatorService.findBySynchronization(origem).get();

    var syncByWork = work.getSynchronizations().stream()
        .anyMatch(sync -> sync.getOrigin()
            .equals(origem) &&
            sync.getUpdatedWorkAt() != null &&
            sync.getUpdatedWorkAt().truncatedTo(ChronoUnit.SECONDS).equals(updateAt.truncatedTo(ChronoUnit.SECONDS)));

    var chapterSync = work.getChapters()
        .stream().anyMatch(chapter -> chapter.getScanlator().equals(scanlator) && !chapter.getSynced());

    return syncByWork && !chapterSync;
  }

  public void updatingSyncWorkTime(Work work, SynchronizationOriginType origem, OffsetDateTime createAt,
                                   OffsetDateTime updateAt) {
    log.debug("--> [SynchronizationBase][syncWorkTime] Syncing work time");
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(createAt, VALIDATION_ERROR_CREATE_AT_IS_NULL);
    Objects.requireNonNull(updateAt, VALIDATION_ERROR_UPDATE_AT_IS_NULL);

    work.getSynchronizations().stream()
        .filter(sync -> sync.getOrigin().equals(origem))
        .findFirst().ifPresent(sync -> {
          sync.setCreatedWorkAt(createAt.truncatedTo(ChronoUnit.SECONDS));
          sync.setUpdatedWorkAt(updateAt.truncatedTo(ChronoUnit.SECONDS));
        });
  }

  public void syncLinks(Work work,
                        List<SynchronizationLinks> links) {
    log.debug("--> [SynchronizationBase][syncLinks] Syncing links");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(links, VALIDATION_ERROR_TITLE_LIST_IS_NULL);

    if (work.getLinks() == null) {
      work.setLinks(new ArrayList<>());
    }

    links.forEach(link -> {
      if (link.getSiteType() == null || link.getLink() == null) {
        return;
      }

      if (link.getLink().isBlank()) {
        throw new IllegalArgumentException(VALIDATION_ERROR_SITE_LINK_IS_BLANK);
      }

      if (!work.hasSiteType(link.getSiteType())) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(link.getSiteType().name()).orElse(null))
                .link(link.getLink())
                .code(link.getSiteType())
                .work(work)
                .build());
      }
    });
  }

  public void syncTags(Work work,
                       List<SynchronizationTags> tags) {
    log.debug("--> [SynchronizationBase][syncTags] Syncing tags");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(tags, VALIDATION_ERROR_TAG_LIST_IS_NULL);

    if (work.getTags() == null) {
      work.setTags(new ArrayList<>());
    }

    tags.forEach(tag -> {
      if (tag.getGroup() == null || tag.getName() == null || tag.getName().isBlank()) {
        return;
      }

      if (!work.hasTagWithNameOrAlias(tag.getGroup(), tag.getName())) {
        work.getTags().add(
            WorkTag.builder()
                .tag(tagService.findOrCreate(tag.getGroup(), tag.getName()))
                .work(work)
                .build());
      }
    });
  }

  public void syncAuthors(Work work,
                          List<Author> authors) {
    log.debug("--> [SynchronizationBase][syncAuthors] Syncing authors");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(authors, VALIDATION_ERROR_AUTHOR_LIST_IS_NULL);

    if (work.getAuthors() == null) {
      work.setAuthors(new ArrayList<>());
    }

    authors.forEach(author -> {
      if (!work.hasAuthorOfType(author.getType(), author.getName()) && !author.getName().isBlank()) {
        work.getAuthors().add(
            WorkAuthor.builder()
                .author(authorService.findOrCreate(author))
                .work(work)
                .build());
      }
    });
  }

  public void syncCover(Work work,
                        String url,
                        String filename,
                        SynchronizationOriginType origin,
                        CoverType size
  ) throws IOException, InterruptedException {
    log.debug("--> [SynchronizationBase][syncCover] Syncing cover");
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);

    if (work.getCovers() == null) {
      work.setCovers(new ArrayList<>());
    }

    if (work.hasCoverWithOriginAndSize(origin, size)) {
      log.debug("--> [SynchronizationBase][syncCover] Work already has cover with origin ({}) and size ({})", origin,
          size);
      return;
    }

    var extension = "." + filename.split("\\.")[1];
    var slug = work.getPublicationDemographic().name().toLowerCase()
        + "/" + work.getSlug()
        + "/covers/";

    var scanlator = Objects.requireNonNull(scanlatorService.findBySynchronization(origin)).get();
    var coverName = scanlator.getCode().toLowerCase() + "_cover_" + size.name().toLowerCase() + extension;

    work.getCovers().add(
        WorkCover.builder()
            .work(work)
            .origin(origin)
            .size(size)
            .fileName(coverName)
            .isOfficial(false)
            .build()
    );

    externalFileService.downloadExternalPublicObjectAndUploadToStorage(
        url,
        coverName,
        slug
    );
  }

  public void syncRelationship(Work work, SynchronizationOriginType origin, String externalId) {
    log.debug("--> [SynchronizationBase][syncRelationship] Syncing relationship");

    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(origin, VALIDATION_ERROR_SYNCHRONIZATION_ORIGIN_IS_NULL);
    Objects.requireNonNull(externalId, VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_NULL);

    if (externalId.isBlank()) {
      throw new IllegalArgumentException(VALIDATION_ERROR_EXTERNAL_WORK_ID_IS_BLANK);
    }

    if (work.getRelationship() == null) {
      var rel = workService.findBySynchronizationExternalID(
              externalId,
              origin)
          .orElse(null);
      if (rel != null) {
        work.setRelationship(rel);
        rel.setRelationship(work);
        workService.save(rel);
        workService.save(work);
      }
    }
  }

  public long jwtExtractExpiration(String jwt) {
    log.debug("--> [SynchronizationBase][jwtExtractExpiration] Extracting expiration from JWT");
    String[] parts = jwt.split("\\.");
    String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(payload);
      return node.get("exp").asLong();
    } catch (Exception e) {
      log.error("[SynchronizationBase][jwtExtractExpiration] Error reading exp from JWT.: {}", e.getMessage());
      throw new RuntimeException("Error reading exp from JWT.", e);
    }
  }

  public boolean isJwtTokenExpiring(String accessToken, long accessTokenExp) {
    if (accessToken == null) {
      return true;
    }

    long now = Instant.now().getEpochSecond();
    return now >= (accessTokenExp - 60);
  }
}
