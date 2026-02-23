package dev.williancorrea.manhwa.reader.synchronization.mangadex;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkContentRating;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLinkRepository;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.features.work.synopsis.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.title.WorkTitle;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexData;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MangaDexMapperService {

  public final WorkService workService;
  public final LanguageService languageService;
  public final WorkLinkRepository workLinkRepository;

  public Work toEntity(MangaDexData dto) {
    try {
      Work work = findWork(dto);

      syncSynchronization(work, dto);
      syncTitle(work, dto);
      syncSynopses(work, dto);
      syncLinks(work, dto);
      syncAttributes(work, dto);

      work.setUpdatedAt(OffsetDateTime.now());
      return work;
    } catch (Exception e) {
      //TODO: Notificar que deu ruim de alguma forma
      log.error("Error mapping MangaDex data to Work entity", e);
      throw e;
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
        .isOfficial(dto.getAttributes().getOriginalLanguage().equals(key))
        .title(value)
        .build()));

    //Alt-Titles
    dto.getAttributes()
        .getAltTitles()
        .forEach(item -> item.forEach((key, value) -> titles.add(WorkTitle.builder()
            .work(work)
            .language(languageService.findOrCreate(key, SynchronizationOriginType.MANGADEX))
            .isOfficial(dto.getAttributes().getOriginalLanguage().equals(key))
            .title(value)
            .build())));

    titles.forEach(item -> {
      AtomicBoolean found = new AtomicBoolean(false);
      work.getTitles().forEach(obj -> {
        if (obj.getTitle().equals(item.getTitle())) {
          found.set(true);
        }
      });
      if (!found.get()) {
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
    if(dto.getAttributes().getContentRating() != null) {
      work.setContentRating(WorkContentRating.valueOf(dto.getAttributes().getContentRating().toUpperCase()));
    }
  }


  //TODO: Gravar o ID das busca nas Scans


//    manga.setId(dto.getId());
//    manga.setType(dto.getType());
//    manga.setAttributes(toAttributes(dto.getAttributes()));
//    manga.setRelationships(dto.getRelationships().stream()
//        .map(rel -> toRelationship(rel, manga))
//        .collect(Collectors.toList()));
//
//}
//
//  private MangaAttributes toAttributes(MangaDexAttributes dto) {
//    MangaAttributes attributes = new MangaAttributes();
//    attributes.setTitle(dto.getTitle());
//    attributes.setAltTitles(dto.getAltTitles().stream()
//        .map(map -> map.values().iterator().next())
//        .collect(Collectors.toList()));
//    attributes.setDescription(dto.getDescription());
//    attributes.setIsLocked(dto.getIsLocked());
//    attributes.setLinks(toLinks(dto.getLinks()));
//    attributes.setOriginalLanguage(dto.getOriginalLanguage());
//    attributes.setLastVolume(dto.getLastVolume());
//    attributes.setLastChapter(dto.getLastChapter());
//    attributes.setPublicationDemographic(dto.getPublicationDemographic());
//    attributes.setStatus(dto.getStatus());
//    attributes.setYear(dto.getYear());
//    attributes.setContentRating(dto.getContentRating());
//    attributes.setState(dto.getState());
//    attributes.setChapterNumbersResetOnNewVolume(dto.getChapterNumbersResetOnNewVolume());
//    attributes.setCreatedAt(dto.getCreatedAt());
//    attributes.setUpdatedAt(dto.getUpdatedAt());
//    attributes.setVersion(dto.getVersion());
//    attributes.setAvailableTranslatedLanguages(dto.getAvailableTranslatedLanguages());
//    attributes.setLatestUploadedChapter(dto.getLatestUploadedChapter());
//
//    return attributes;
//  }
//
//  private MangaLinks toLinks(MangaDexLinks dto) {
//    return MangaLinks.builder()
//        .anilist(dto.getAnilist())
//        .animePlanet(dto.getAnimePlanet())
//        .bookWalker(dto.getBookWalker())
//        .kitsu(dto.getKitsu())
//        .mangaUpdates(dto.getMangaUpdates())
//        .myAnimeList(dto.getMyAnimeList())
//        .raw(dto.getRaw())
//        .build();
//  }
//
//  private MangaRelationship toRelationship(MangaDexRelationship dto, Manga manga) {
//    MangaRelationship relationship = new MangaRelationship();
//    relationship.setId(dto.getId());
//    relationship.setType(dto.getType());
//    relationship.setAttributes(toRelationshipAttributes(dto.getAttributes()));
//    relationship.setManga(manga);
//    return relationship;
//  }
//
//  private RelationshipAttributes toRelationshipAttributes(MangaDexRelationshipAttributes dto) {
//    RelationshipAttributes attributes = new RelationshipAttributes();
//    attributes.setName(dto.getName());
//    attributes.setImageUrl(dto.getImageUrl());
//    attributes.setBiography(dto.getBiography());
//    attributes.setTwitter(dto.getTwitter());
//    attributes.setPixiv(dto.getPixiv());
//    attributes.setMelonBook(dto.getMelonBook());
//    attributes.setFanBox(dto.getFanBox());
//    attributes.setBooth(dto.getBooth());
//    attributes.setNamicomi(dto.getNamicomi());
//    attributes.setNicoVideo(dto.getNicoVideo());
//    attributes.setSkeb(dto.getSkeb());
//    attributes.setFantia(dto.getFantia());
//    attributes.setTumblr(dto.getTumblr());
//    attributes.setYoutube(dto.getYoutube());
//    attributes.setWeibo(dto.getWeibo());
//    attributes.setNaver(dto.getNaver());
//    attributes.setWebsite(dto.getWebsite());
//    attributes.setVersion(dto.getVersion());
//    attributes.setDescription(dto.getDescription());
//    attributes.setVolume(dto.getVolume());
//    attributes.setFileName(dto.getFileName());
//    attributes.setLocale(dto.getLocale());
//
//    return attributes;
//  }
}