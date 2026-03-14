package dev.williancorrea.manhwa.reader.scraper.mangadex;


import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_IS_NULL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorService;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkContentRating;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.scraper.base.Scraper;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperBase;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationLinks;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTags;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MangaDexMapperService implements Scraper<MangaDexData> {

  public final WorkService workService;
  public final ScraperBase scraperBase;
  public final LanguageService languageService;

  public final AuthorService authorService;
  public final ExternalFileService externalFileService;

  public MangaDexMapperService(@Lazy WorkService workService,
                               @Lazy ScraperBase scraperBase,
                               @Lazy LanguageService languageService,

                               @Lazy AuthorService authorService,
                               @Lazy ExternalFileService externalFileService) {


    this.workService = workService;
    this.scraperBase = scraperBase;
    this.languageService = languageService;

    this.authorService = authorService;
    this.externalFileService = externalFileService;
  }

  @Value("${synchronization.mangadex.site.url}")
  private String MANDADEX_URL_COVERS;

  public Work toEntity(MangaDexData dto) {
    log.info("--> [MangaDexMapperService][toEntity] Mapping MangaDex data to Work: {}",
        dto.getAttributes().getTitle().values().stream().findAny().orElse(""));
    Work work = null;
    try {
      work = scraperBase.findWorkOrCreate(dto.getId(), SynchronizationOriginType.MANGADEX);

      prepareSynchronization(work, dto);
      prepareSyncTitle(work, dto);

      prepareSyncSynopses(work, dto);
      prepareSyncLinks(work, dto);
      prepareSyncAttributes(work, dto);
      prepareSyncTags(work, dto);
      prepareSyncAuthors(work, dto);
      prepareSyncCover(work, dto);

      work.setUpdatedAt(OffsetDateTime.now());
      return work;
    } catch (Exception e) {
      scraperBase.syncWorkError(
          SynchronizationOriginType.MANGADEX,
          work != null && work.getId() == null ? null : Objects.requireNonNull(work).getId().toString(),
          dto.getId(),
          dto.getAttributes().getTitle().values().stream().findAny().orElse(""),
          e.getMessage()
      );
      throw new RuntimeException("Error mapping MangaDex data to Work entity");
    }
  }

  @Override
  public void synchronizeByExternalId(String externalId) {
    log.debug("--> [MangaDexMapperService][synchronizeByExternalId] Starting synchronization with Mangadex for external id: {}", externalId);
  }

  @Override
  public void synchronizeByExternalId(MangaDexData workDto) {
    log.info(
        "--> [MangaDexMapperService][synchronizeByExternalId] Starting synchronization with Mangadex for external work: {}",
        workDto.getId());
  }

  @Override
  public void ScheduledSynchronization() {
    log.info("--> [MangaDexMapperService][ScheduledSynchronization] Starting synchronization with Mangadex");
  }

  @Override
  public void synchronizeByWork(Work work) {
    log.info("--> [MediocrescanService][synchronizeByWork] Starting synchronization with Mediocrescan for work: {}",
        work.getTitles().getFirst().getTitle());
  }

  @Override
  public void prepareSyncTitle(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getTitles() == null) {
      work.setTitles(new ArrayList<>());
    }

    var titles = new ArrayList<SynchronizationTitle>();

    // Title
    dto.getAttributes().getTitle().forEach((key, value) -> titles.add(
        SynchronizationTitle.builder()
            .title(value)
            .language(key)
            .origin(SynchronizationOriginType.MANGADEX)
            .build()
    ));

    //Alt-Titles
    dto.getAttributes()
        .getAltTitles()
        .forEach(item -> item.forEach((key, value) -> titles.add(
            SynchronizationTitle.builder()
                .title(value)
                .language(key)
                .origin(SynchronizationOriginType.MANGADEX)
                .build()
        )));

    scraperBase.syncTitle(work, titles);
  }

  @Override
  public void prepareSyncLinks(Work work, MangaDexData dto) {
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(dto, VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL);

    List<SynchronizationLinks> links = List.of(
        SynchronizationLinks.builder()
            .siteType(SiteType.MANGADEX)
            .link(dto.getId())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.ANI_LIST)
            .link(dto.getAttributes().getLinks().getAnilist())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.ANIME_PLANET)
            .link(dto.getAttributes().getLinks().getAnimePlanet())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.NOVEL_UPDATES)
            .link(dto.getAttributes().getLinks().getNovelUpdates())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.MY_ANIME_LIST)
            .link(dto.getAttributes().getLinks().getMyAnimeList())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.MANGA_UPDATES)
            .link(dto.getAttributes().getLinks().getMangaUpdates())
            .build(),
        SynchronizationLinks.builder()
            .siteType(SiteType.KITSU)
            .link(dto.getAttributes().getLinks().getKitsu())
            .build()
    );

    scraperBase.syncLinks(work, links);
  }

  @Override
  public void prepareSyncTags(Work work, MangaDexData dto) {
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(dto, VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL);

    log.debug("--> [MangaDexMapperService][syncTags] Syncing tags");
    var tags = new ArrayList<SynchronizationTags>();
    dto.getAttributes().getTags().forEach(tag -> {
      var group = TagGroupType.valueOf(tag.getAttributes().getGroup().toUpperCase());
      var name = tag.getAttributes().getName().values().stream().findAny().orElse("");
      tags.add(SynchronizationTags.builder()
          .group(group)
          .name(name)
          .build());
    });
    scraperBase.syncTags(work, tags);
  }

  @Override
  public void prepareSyncAuthors(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    var authors = new ArrayList<Author>();

    dto.getRelationships().forEach(author -> {
      if (Arrays.asList(AuthorType.AUTHOR.name(), AuthorType.ARTIST.name()).contains(author.getType().toUpperCase())) {
        authors.add(Author.builder()
            .name(author.getAttributes().getName())
            .type(AuthorType.valueOf(author.getType().toUpperCase()))
            .biography(author.getAttributes().getBiography().values().stream().findAny().orElse(null))
            .twitter(author.getAttributes().getTwitter())
            .pixiv(author.getAttributes().getPixiv())
            .melonBook(author.getAttributes().getMelonBook())
            .fanBox(author.getAttributes().getFanBox())
            .booth(author.getAttributes().getBooth())
            .namicomi(author.getAttributes().getNamicomi())
            .nicoVideo(author.getAttributes().getNicoVideo())
            .skeb(author.getAttributes().getSkeb())
            .fanBox(author.getAttributes().getFanBox())
            .tumblr(author.getAttributes().getTumblr())
            .youtube(author.getAttributes().getYoutube())
            .weibo(author.getAttributes().getWeibo())
            .naver(author.getAttributes().getNaver())
            .website(author.getAttributes().getWebsite())
            .build()
        );
      }
    });
    scraperBase.syncAuthors(work, authors);
  }

  @Override
  public void prepareSynchronization(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    scraperBase.syncSynchronization(
        work,
        dto.getId(),
        SynchronizationOriginType.MANGADEX,
        null
    );
  }

  @Override
  public void prepareSyncSynopses(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    List<SynchronizationSynopses> synopses = new ArrayList<>();
    dto.getAttributes().getDescription().forEach((key, value) -> synopses.add(
        SynchronizationSynopses.builder()
            .language(key)
            .origin(SynchronizationOriginType.MANGADEX)
            .description(value)
            .build()));

    scraperBase.syncSynopses(
        work,
        synopses);
  }

  @Override
  public void prepareSyncAttributes(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    WorkContentRating contentRating = dto.getAttributes().getContentRating() != null
        ? WorkContentRating.valueOf(dto.getAttributes().getContentRating().toUpperCase())
        : null;

    var originalLanguage = languageService.findOrCreate(
        dto.getAttributes().getOriginalLanguage(),
        SynchronizationOriginType.MANGADEX
    );

    scraperBase.syncAttributes(
        work,
        getWorkPublicationDemographic(dto),
        WorkType.valueOf(dto.getType().toUpperCase()),
        WorkStatus.valueOf(dto.getAttributes().getStatus().toUpperCase()),
        dto.getAttributes().getTitle().values().stream().findAny().orElse(""),
        false,
        dto.getAttributes().getYear(),
        originalLanguage,
        contentRating,
        dto.getAttributes().getChapterNumbersResetOnNewVolume()
    );
  }

  @Override
  public void prepareSyncCover(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    dto.getRelationships().stream().filter(rel -> rel.getType().equals("cover_art")).findFirst().ifPresent(cover -> {
      try {
        scraperBase.syncCover(work,
            MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName(),
            cover.getAttributes().getFileName(),
            SynchronizationOriginType.MANGADEX,
            CoverType.HIGH
        );

        scraperBase.syncCover(work,
            MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".512.jpg",
            cover.getAttributes().getFileName(),
            SynchronizationOriginType.MANGADEX,
            CoverType.MEDIUM);

        scraperBase.syncCover(work,
            MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".256.jpg",
            cover.getAttributes().getFileName(),
            SynchronizationOriginType.MANGADEX,
            CoverType.LOW);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void prepareSyncRelationships(Work work, MangaDexData workDto) {
    log.debug("--> [MangaDexMapperService][prepareSyncRelationships] Syncing relationships");
  }

  @Override
  public void prepareSyncChapters(Work work, MangaDexData workDto) {
    log.debug("--> [MangaDexMapperService][prepareSyncChapters] Syncing chapters");
  }

  private WorkPublicationDemographic getWorkPublicationDemographic(MangaDexData dto) {
    WorkPublicationDemographic demographic = WorkPublicationDemographic.UNKNOWN;
    if (dto.getAttributes().getPublicationDemographic() != null &&
        !dto.getAttributes().getPublicationDemographic().isEmpty()) {
      demographic = WorkPublicationDemographic.valueOf(dto.getAttributes().getPublicationDemographic().toUpperCase());
    }
    return demographic;
  }
}