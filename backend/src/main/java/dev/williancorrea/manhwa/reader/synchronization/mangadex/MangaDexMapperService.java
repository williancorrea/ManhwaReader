package dev.williancorrea.manhwa.reader.synchronization.mangadex;


import static dev.williancorrea.manhwa.reader.synchronization.base.SynchronizationErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL;
import static dev.williancorrea.manhwa.reader.synchronization.base.SynchronizationErrorMessage.VALIDATION_ERROR_WORK_IS_NULL;

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
import dev.williancorrea.manhwa.reader.features.work.WorkAuthor;
import dev.williancorrea.manhwa.reader.features.work.WorkContentRating;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.synchronization.base.Synchronization;
import dev.williancorrea.manhwa.reader.synchronization.base.SynchronizationBase;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationLinks;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationTags;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MangaDexMapperService implements Synchronization<MangaDexData> {

  public final WorkService workService;
  public final SynchronizationBase synchronizationBase;
  public final LanguageService languageService;

  public final AuthorService authorService;
  public final ExternalFileService externalFileService;

  public MangaDexMapperService(@Lazy WorkService workService,
                               @Lazy SynchronizationBase synchronizationBase,
                               @Lazy LanguageService languageService,

                               @Lazy AuthorService authorService,
                               @Lazy ExternalFileService externalFileService) {


    this.workService = workService;
    this.synchronizationBase = synchronizationBase;
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
      work = synchronizationBase.findWorkOrCreate(dto.getId(), SynchronizationOriginType.MANGADEX);

      prepareSynchronization(work, dto);
      prepareSyncTitle(work, dto);

      prepareSyncSynopses(work, dto);
      prepareSyncLinks(work, dto);
      prepareSyncAttributes(work, dto);
      prepareSyncTags(work, dto);
      prepareSyncAuthors(work, dto);
      syncCover(work, dto);

      work.setUpdatedAt(OffsetDateTime.now());
      return work;
    } catch (Exception e) {
      synchronizationBase.syncWorkError(
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

    synchronizationBase.syncTitle(work, titles);
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

    synchronizationBase.syncLinks(work, links);
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
    synchronizationBase.syncTags(work, tags);
  }

  @Override
  public void prepareSyncAuthors(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getAuthors() == null) {
      work.setAuthors(new ArrayList<>());
    }

    dto.getRelationships().forEach(author -> {
      if (Arrays.asList(AuthorType.AUTHOR.name(), AuthorType.ARTIST.name()).contains(author.getType().toUpperCase())) {

        var type = AuthorType.valueOf(author.getType().toUpperCase());
        var name = author.getAttributes().getName();

        if (!work.getAuthorsContains(type, name) && !name.isBlank()) {
          work.getAuthors().add(
              WorkAuthor.builder()
                  .author(authorService.findOrCreate(Author.builder()
                      .name(author.getAttributes().getName())
                      .type(type)
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
                      .build()))
                  .work(work)
                  .build()
          );
        }
      }
    });
  }

  @Override
  public void prepareSynchronization(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    synchronizationBase.syncSynchronization(
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

    synchronizationBase.syncSynopses(
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

    synchronizationBase.syncAttributes(
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

  private void syncCover(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    dto.getRelationships().stream().filter(rel -> rel.getType().equals("cover_art")).findFirst().ifPresent(cover -> {
      var extension = "." + cover.getAttributes().getFileName().split("\\.")[1];
      try {

        if (work.getCoverMedium() == null) {
          work.setCoverMedium("cover_512" + extension);
          externalFileService.downloadWithAuthAndUpload(
              MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".512.jpg",
              work.getCoverMedium(),
              work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
          );
        }

        if (work.getCoverLow() == null) {
          work.setCoverLow("cover_256" + extension);
          externalFileService.downloadWithAuthAndUpload(
              MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".256.jpg",
              work.getCoverLow(),
              work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
          );
        }

        if (work.getCoverHigh() == null) {
          work.setCoverHigh("cover" + extension);
          externalFileService.downloadWithAuthAndUpload(
              MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName(),
              work.getCoverHigh(),
              work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
          );
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
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