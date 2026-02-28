package dev.williancorrea.manhwa.reader.synchronization.mangadex;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.williancorrea.manhwa.reader.features.author.Author;
import dev.williancorrea.manhwa.reader.features.author.AuthorService;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
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
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLinkRepository;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexData;
import dev.williancorrea.manhwa.reader.utils.RemoveAccentuationUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MangaDexMapperService {

  public final WorkService workService;
  public final LanguageService languageService;
  public final WorkLinkRepository workLinkRepository;
  public final TagService tagService;
  public final AuthorService authorService;
  public final ExternalFileService externalFileService;

  @Value("${synchronization.mangadex.api.url}")
  private String MANDADEX_URL_COVERS;

  public Work toEntity(MangaDexData dto) {
    try {
      Work work = findWork(dto);

      syncSynchronization(work, dto);
      syncTitle(work, dto);
      syncSynopses(work, dto);
      syncLinks(work, dto);
      syncAttributes(work, dto);
      syncTags(work, dto);
      syncAuthors(work, dto);
      syncCover(work, dto);

      work.setUpdatedAt(OffsetDateTime.now());
      return work;
    } catch (Exception e) {
      //TODO: Notificar que deu ruim de alguma forma
      log.error("Error mapping MangaDex data to Work entity", e);
      throw new RuntimeException("Error mapping MangaDex data to Work entity");
    }
  }

  private Work findWork(MangaDexData dto) {
    Objects.requireNonNull(dto);
    var work = workService.findBySynchronizationExternalID(dto.getId()).orElse(null);
    if (work == null) {
      var tittle = dto.getAttributes().getTitle().values().stream().findAny().orElse(null);
      if (tittle != null) {
        work = workService.findByTitle(tittle).orElse(Work.builder()
            .createdAt(OffsetDateTime.now())
            .disabled(false)
            .build());
      }
      return work;
    }
    return work;
  }

  private void syncLinks(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getLinks() == null) {
      work.setLinks(new ArrayList<>());
    }

    if (!work.getLinksContains(SiteType.MANGADEX)) {
      work.getLinks().add(
          WorkLink.builder()
              .site(workLinkRepository.findBySiteCode(SiteType.MANGADEX.name()).orElse(null))
              .link(dto.getId())
              .code(SiteType.MANGADEX)
              .work(work)
              .build());
    }

    if (!work.getLinksContains(SiteType.ANI_LIST)) {
      var link = dto.getAttributes().getLinks().getAnilist();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.ANI_LIST.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getAnilist())
                .code(SiteType.ANI_LIST)
                .work(work)
                .build()
        );
      }
    }

    if (!work.getLinksContains(SiteType.ANIME_PLANET)) {
      var link = dto.getAttributes().getLinks().getAnimePlanet();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.ANIME_PLANET.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getAnimePlanet())
                .code(SiteType.ANIME_PLANET)
                .work(work)
                .build()
        );
      }
    }

    if (!work.getLinksContains(SiteType.NOVEL_UPDATES)) {
      var link = dto.getAttributes().getLinks().getNovelUpdates();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.NOVEL_UPDATES.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getNovelUpdates())
                .code(SiteType.NOVEL_UPDATES)
                .work(work)
                .build()
        );
      }
    }

    if (!work.getLinksContains(SiteType.MY_ANIME_LIST)) {
      var link = dto.getAttributes().getLinks().getMyAnimeList();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.MY_ANIME_LIST.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getMyAnimeList())
                .code(SiteType.MY_ANIME_LIST)
                .work(work)
                .build()
        );
      }
    }

    if (!work.getLinksContains(SiteType.MANGA_UPDATES)) {
      var link = dto.getAttributes().getLinks().getMangaUpdates();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.MANGA_UPDATES.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getMangaUpdates())
                .code(SiteType.MANGA_UPDATES)
                .work(work)
                .build()
        );
      }
    }

    if (!work.getLinksContains(SiteType.KITSU)) {
      var link = dto.getAttributes().getLinks().getKitsu();
      if (link != null && !link.trim().isEmpty()) {
        work.getLinks().add(
            WorkLink.builder()
                .site(workLinkRepository.findBySiteCode(SiteType.KITSU.name()).orElse(null))
                .link(dto.getAttributes().getLinks().getKitsu())
                .code(SiteType.KITSU)
                .work(work)
                .build()
        );
      }
    }
  }

  private void syncTags(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getTags() == null) {
      work.setTags(new ArrayList<>());
    }

    dto.getAttributes().getTags().forEach(tag -> {

      var group = TagGroupType.valueOf(tag.getAttributes().getGroup().toUpperCase());
      var name = tag.getAttributes().getName().values().stream().findAny().orElse("");

      if (!work.getTagsContains(group, name) && !name.isBlank()) {
        work.getTags().add(
            WorkTag.builder()
                .tag(tagService.findOrCreate(group, name))
                .work(work)
                .build());
      }
    });
  }

  private void syncAuthors(Work work, MangaDexData dto) {
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

  private void syncSynchronization(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getSynchronizations() == null) {
      work.setSynchronizations(new ArrayList<>());
    }

    if (!work.getSynchronizationsContains(SynchronizationOriginType.MANGADEX)) {
      work.getSynchronizations().add(
          WorkSynchronization.builder()
              .externalId(dto.getId())
              .origin(SynchronizationOriginType.MANGADEX)
              .work(work)
              .build());
    }
  }

  private void syncTitle(@NotNull Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getTitles() == null) {
      work.setTitles(new ArrayList<>());
    }

    List<WorkTitle> titles = new ArrayList<>();

    // Title
    dto.getAttributes().getTitle().forEach((key, value) -> titles.add(WorkTitle.builder()
        .work(work)
        .language(languageService.findOrCreate(key, SynchronizationOriginType.MANGADEX))
        .isOfficial(false)
        .title(value)
        .build()));

    //Alt-Titles
    dto.getAttributes()
        .getAltTitles()
        .forEach(item -> item.forEach((key, value) -> titles.add(WorkTitle.builder()
            .work(work)
            .language(languageService.findOrCreate(key, SynchronizationOriginType.MANGADEX))
            .isOfficial(false)
            .title(value)
            .build())));

    titles.forEach(item -> {
      AtomicBoolean found = new AtomicBoolean(false);
      work.getTitles().forEach(obj -> {
        if (obj.getTitle().equalsIgnoreCase(item.getTitle())) {
          found.set(true);
        }
      });
      if (!found.get()) {
        if (work.getTitles().isEmpty()) {
          item.setIsOfficial(true);
        }
        work.getTitles().add(item);
      }
    });
  }

  private void syncSynopses(@NotNull Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getSynopses() == null) {
      work.setSynopses(new ArrayList<>());
    }

    List<WorkSynopsis> synopses = new ArrayList<>();

    dto.getAttributes().getDescription().forEach((key, value) -> synopses.add(WorkSynopsis.builder()
        .work(work)
        .language(languageService.findOrCreate(key, SynchronizationOriginType.MANGADEX))
        .description(value)
        .build()));

    synopses.forEach(item -> {
      AtomicBoolean found = new AtomicBoolean(false);
      work.getSynopses().forEach(obj -> {
        if (obj.getLanguage().equals(item.getLanguage())) {
          found.set(true);
        }
      });
      if (!found.get()) {
        work.getSynopses().add(item);
      }
    });
  }

  private void syncAttributes(@NotNull Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    work.setType(WorkType.valueOf(dto.getType().toUpperCase()));
    work.setStatus(WorkStatus.valueOf(dto.getAttributes().getStatus().toUpperCase()));
    work.setOriginalLanguage(
        languageService.findOrCreate(dto.getAttributes().getOriginalLanguage(), SynchronizationOriginType.MANGADEX));
    work.setReleaseYear(dto.getAttributes().getYear());

    if (dto.getAttributes().getPublicationDemographic() != null) {
      work.setPublicationDemographic(
          WorkPublicationDemographic.valueOf(dto.getAttributes().getPublicationDemographic().toUpperCase()));
    }

    if (dto.getAttributes().getContentRating() != null) {
      work.setContentRating(WorkContentRating.valueOf(dto.getAttributes().getContentRating().toUpperCase()));
    }
    work.setChapterNumbersResetOnNewVolume(dto.getAttributes().getChapterNumbersResetOnNewVolume());
  }

  private void syncCover(Work work, MangaDexData dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getSlug() == null || work.getSlug().isEmpty()) {
      var title = work.getTitles()
          .stream().filter(title1 -> Boolean.TRUE.equals(title1.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findAny().orElse("GENERATED" + UUID.randomUUID());
      title = RemoveAccentuationUtils.normalize(title).toLowerCase();
      work.setSlug(title);
    }

    dto.getRelationships().stream().filter(rel -> rel.getType().equals("cover_art")).findFirst().ifPresent(cover -> {
      var extension = "." + cover.getAttributes().getFileName().split("\\.")[1];
      try {

        if (work.getCoverMedium() != null && !work.getCoverMedium().isEmpty()) {
          work.setCoverMedium("cover_512" + extension);
          externalFileService.downloadWithAuthAndUpload(
              MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".512.jpg",
              work.getCoverMedium(),
              work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
          );
        }

        if (work.getCoverLow() != null && !work.getCoverLow().isEmpty()) {
          work.setCoverLow("cover_256" + extension);
          externalFileService.downloadWithAuthAndUpload(
              MANDADEX_URL_COVERS + "/covers/" + dto.getId() + "/" + cover.getAttributes().getFileName() + ".256.jpg",
              work.getCoverLow(),
              work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
          );
        }

        if (work.getCoverHigh() != null && !work.getCoverHigh().isEmpty()) {
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
}