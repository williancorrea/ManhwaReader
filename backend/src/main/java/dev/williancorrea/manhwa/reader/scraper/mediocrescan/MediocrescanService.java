package dev.williancorrea.manhwa.reader.scraper.mediocrescan;

import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL;
import static dev.williancorrea.manhwa.reader.scraper.base.ScraperErrorMessage.VALIDATION_ERROR_WORK_IS_NULL;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.chapter.ChapterService;
import dev.williancorrea.manhwa.reader.features.chapter.notify.ChapterNotify;
import dev.williancorrea.manhwa.reader.features.chapter.notify.ChapterNotifyService;
import dev.williancorrea.manhwa.reader.features.chapter.notify.ChapterNotifyType;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.page.Page;
import dev.williancorrea.manhwa.reader.features.page.PageService;
import dev.williancorrea.manhwa.reader.features.page.PageType;
import dev.williancorrea.manhwa.reader.features.scanlator.ScanlatorService;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.volume.VolumeService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.scraper.base.Scraper;
import dev.williancorrea.manhwa.reader.scraper.base.ScraperBase;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTags;
import dev.williancorrea.manhwa.reader.scraper.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.client.MediocrescanClient;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.capitulo.Mediocrescan_CapituloDTO;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.login.Mediocrescan_LoginDTO;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.login.Mediocrescan_RefreshTokenDTO;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.obra.Mediocrescan_ObraDTO;
import dev.williancorrea.manhwa.reader.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MediocrescanService implements Scraper<Mediocrescan_ObraDTO> {

  public static final String UNKNOWN_LANGUAGE = "xx-XX";
  public final MediocrescanClient mediocrescanClient;

  public final WorkService workService;
  public final ScraperBase scraperBase;
  public final LanguageService languageService;
  public final ScanlatorService scanlatorService;


  public final ExternalFileService externalFileService;
  public final ChapterService chapterService;

  public final PageService pageService;
  public final VolumeService volumeService;
  public final ChapterNotifyService chapterNotifyService;

  ExecutorService executorWorks = Executors.newFixedThreadPool(10);


  public MediocrescanService(@Lazy WorkService workService,
                             @Lazy ScraperBase scraperBase,
                             @Lazy LanguageService languageService,
                             @Lazy ScanlatorService scanlatorService,

                             @Lazy MediocrescanClient mediocrescanClient,
                             @Lazy ExternalFileService externalFileService,
                             @Lazy ChapterService chapterService,
                             @Lazy PageService pageService,
                             @Lazy VolumeService volumeService,
                             @Lazy ChapterNotifyService chapterNotifyService) {

    this.workService = workService;
    this.scraperBase = scraperBase;
    this.languageService = languageService;
    this.scanlatorService = scanlatorService;

    this.mediocrescanClient = mediocrescanClient;
    this.externalFileService = externalFileService;
    this.chapterService = chapterService;
    this.pageService = pageService;
    this.volumeService = volumeService;
    this.chapterNotifyService = chapterNotifyService;

  }

  @Value("${synchronization.mediocrescan.cdn.url}")
  private String mediocreScanUrlCDN;

  @Value("${synchronization.mediocrescan.api.url}")
  private String mediocreScanUrlAPI;

  @Value("${synchronization.mediocrescan.api.username}")
  private String mediocrescanApiUsername;

  @Value("${synchronization.mediocrescan.api.password}")
  private String mediocrescanApiPassword;

  private String accessToken;
  private String refreshToken;
  private long accessTokenExp;

  private synchronized String getToken() {
    if (accessToken == null && refreshToken == null) {
      log.debug("--> [MediocrescanService] Login");

      var tokenResponse =
          mediocrescanClient.login(
              new Mediocrescan_LoginDTO(mediocrescanApiUsername, mediocrescanApiPassword)
          );

      this.accessToken = tokenResponse.getAccessToken();
      this.refreshToken = tokenResponse.getRefreshToken();
      this.accessTokenExp = scraperBase.jwtExtractExpiration(accessToken);
    }

    if (scraperBase.isJwtTokenExpiring(accessToken, accessTokenExp)) {
      log.debug("--> [MediocrescanService] Refresh token");

      var refreshed =
          mediocrescanClient.refreshToken(
              new Mediocrescan_RefreshTokenDTO(refreshToken)
          );

      this.accessToken = refreshed.getAccessToken();
      this.accessTokenExp = scraperBase.jwtExtractExpiration(accessToken);
    }
    return "Bearer " + accessToken;
  }

  @PostConstruct
  @Transactional
  @Override
  public void ScheduledSynchronization() {
    log.info("--> [MediocrescanService][ScheduledSynchronization] Starting synchronization with Mediocrescan");
    new Thread(() -> {
      try {

        //TODO REMOVER
        //    synchronizeByExternalId("2373");
        //    if (true) {
        //      return;
        //    }

        var totalPages = 1;
        for (int i = 0; i < totalPages; i++) {
          log.warn("--> X <-- [MediocrescanService][ScheduledSynchronization] External synchronization page {} of {}",
              i + 1,
              totalPages);
    
    
          /*
          1 - ENGLISH
          2 -
          3 - NOVEL
          4 - SHOUJO
          5 - COMIC
          6 -
          7 -
          8 - YAOI - G
          9 - YURI - L
          10 - HENTAI
          11 -
          12 -
          13 - MANGA
          */

          String titulo = null;
          //      titulo = "Cavaleiro em eterna regressão"; //COMIC
          //      titulo = "Reencarnei no Corpo de um Príncipe Canalha"; //COMIC
          //      titulo = "I Became a Munchkin Skill Thief"; // ENGLISH
          //      titulo = "Irmãs Ki"; // ENGLISH - 1 Caps
          //      titulo = "Necromante! Eu Sou Um Desastre"; //COMIC e NOVEL
          //      titulo = "O Gênio Que Lê O Mundo"; // COMIC - Testando titulos alternativos
          //      titulo = "O Começo Depois do Fim";


          var obras = mediocrescanClient.listarObras(
              getToken(),
              24, //Padrao 24
              i + 1,
              "data_ultimo_cap",
              "1,5",  // 1,3,4
              titulo
          );

          totalPages = obras.getPagination().getTotalPages();
//      obras.getData().forEach(this::synchronizeByExternalId);
//          obras.getData().forEach(item ->
//              synchronizeByExternalId(item.getId().toString())
//          );

          List<CompletableFuture<Void>> futures = obras.getData()
              .stream()
              .map(item ->
                  CompletableFuture.runAsync(() ->
                      synchronizeByExternalId(item), executorWorks)
              ).toList();

          // barreira: espera TODOS terminarem
          CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
      } catch (Exception e) {
        log.error("--> [MediocrescanService][ScheduledSynchronization] Error", e);
      }
    }).start();
  }

  @Transactional
  @Override
  public void synchronizeByWork(Work work) {
    log.info("--> [MediocrescanService][synchronizeByWork] Starting synchronization with Mediocrescan for work: {}",
        work.getTitles().getFirst().getTitle());
  }

  @Transactional
  @Override
  public void synchronizeByExternalId(String externalId) {
    log.info(
        "--> [MediocrescanService][synchronizeByExternalId] Starting synchronization with Mediocrescan for externalId: {}",
        externalId);
    var obra = mediocrescanClient.obterObra(getToken(), externalId);
    if (obra != null) {
      synchronizeByExternalId(obra);
    }
  }

  @Transactional
  @Override
  public void synchronizeByExternalId(Mediocrescan_ObraDTO obra) {
    log.info(
        "--> [MediocrescanService][synchronizeByExternalId] Starting synchronization with Mediocrescan for obra: {} - {}",
        obra.getId(), obra.getNome());

    Work work = null;
    try {
      work = scraperBase.findWorkOrCreate(obra.getId().toString(), SynchronizationOriginType.MEDIOCRESCAN);

      if (scraperBase.isWorkUpdated(work, SynchronizationOriginType.MEDIOCRESCAN, obra.getDataUltimoCap())) {
        log.info("--> [MediocrescanService][synchronizeByExternalId] Work ({}) already updated", obra.getNome());
        return;
      }
      
      //Caso seja a primeira consulta, então busca todos os titulos da obra
      if (work.getId() == null) {
        log.info("--> [MediocrescanService][synchronizeByExternalId] Work ({}) is new, loading more infos (titles...)", obra.getNome());
        obra = mediocrescanClient.obterObra(getToken(), obra.getId().toString());
      }

      prepareSyncTitle(work, obra);
      prepareSyncAttributes(work, obra);
      prepareSynchronization(work, obra);
      prepareSyncSynopses(work, obra);
      prepareSyncTags(work, obra);
      prepareSyncCover(work, obra);

      work.setUpdatedAt(OffsetDateTime.now());
      work = workService.saveAndNotifyIfNew(work, SynchronizationOriginType.MEDIOCRESCAN);

      prepareSyncRelationships(work, obra);
      prepareSyncChapters(work, obra);

      scraperBase.updatingSyncWorkTime(work,
          SynchronizationOriginType.MEDIOCRESCAN,
          obra.getCriadaEm(),
          obra.getDataUltimoCap()
      );

      work.setUpdatedAt(OffsetDateTime.now());
      work = workService.save(work);

      log.info("<-- [MediocrescanService][synchronizeByExternalId] Synchronization completed: {}",
          obra.getNome().trim());
    } catch (Exception e) {
      scraperBase.syncWorkError(
          SynchronizationOriginType.MEDIOCRESCAN,
          work != null && work.getId() == null ? null : Objects.requireNonNull(work).getId().toString(),
          obra.getId().toString(),
          "(" + obra.getFormato().getNome() + ") " + obra.getNome(),
          e.getMessage(),
          Arrays.toString(e.getStackTrace())
      );
    }
  }

  @Override
  public void prepareSyncTitle(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    var titles = new ArrayList<SynchronizationTitle>();
    titles.add(SynchronizationTitle.builder()
        .title(dto.getNome().trim())
        .language(dto.getFormato().getNome().equalsIgnoreCase("ENGLISH") ? "en" : "pt-BR")
        .origin(SynchronizationOriginType.MEDIOCRESCAN)
        .build()
    );

    if (dto.getTituloAlternativo() != null && !dto.getTituloAlternativo().isEmpty()) {
      dto.getTituloAlternativo().forEach(t -> titles.add(SynchronizationTitle.builder()
          .title(t)
          .language(UNKNOWN_LANGUAGE)
          .origin(SynchronizationOriginType.MEDIOCRESCAN)
          .build()));
    }

    scraperBase.syncTitle(work, titles);
  }

  private WorkStatus getWorkStatus(Mediocrescan_ObraDTO dto) {
    log.debug("--> [MediocrescanService][syncAttributes] ({}) Syncing status", dto.getNome());
    return switch (dto.getStatus().getNome()) {
      case "Em Andamento", "Ativo" -> WorkStatus.ONGOING;
      case "Concluído" -> WorkStatus.COMPLETED;
      case "Hiato" -> WorkStatus.HIATUS;
      case "Cancelada" -> WorkStatus.CANCELLED;
      default -> {
        log.error("--> [MediocrescanService][syncAttributes] ({}) Unknown status: {}",
            dto.getNome(),
            dto.getStatus().getNome());
        throw new RuntimeException("Status not found: " + dto.getStatus().getNome());
      }
    };
  }

  private WorkPublicationDemographic getWorkPublicationDemographic(Mediocrescan_ObraDTO dto) {
    log.debug("--> [MediocrescanService][syncAttributes] ({}) Syncing demographic", dto.getNome());

    var demographic = dto.getFormato().getNome().toUpperCase();
    if (demographic.isEmpty()) {
      demographic = WorkPublicationDemographic.UNKNOWN.name();
    }
    if (demographic.equalsIgnoreCase("ENGLISH")) {
      demographic = WorkPublicationDemographic.COMIC.name();
    }
    return WorkPublicationDemographic.valueOf(demographic);
  }

  @Override
  public void prepareSyncAttributes(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncAttributes] ({}) Syncing attributes", dto.getNome());

    var workType = dto.getFormato().getNome().toUpperCase().contains("NOVEL")
        ? WorkType.NOVEL
        : WorkType.MANHWA;

    scraperBase.syncAttributes(
        work,
        getWorkPublicationDemographic(dto),
        workType,
        getWorkStatus(dto),
        dto.getSlug() == null || dto.getSlug().isBlank() ? dto.getNome() : dto.getSlug(),
        dto.getFormato().getNome().equalsIgnoreCase("NOVEL"),
        null,
        null,
        null,
        null
    );
  }

  public void prepareSyncRelationships(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (dto.getObraNovel() != null && dto.getObraNovel().getId() != null) {
      scraperBase.syncRelationship(
          work,
          SynchronizationOriginType.MEDIOCRESCAN,
          dto.getObraNovel().getId().toString()
      );
    } else if (dto.getObraOriginal() != null && dto.getObraOriginal().getId() != null) {
      scraperBase.syncRelationship(
          work,
          SynchronizationOriginType.MEDIOCRESCAN,
          dto.getObraOriginal().getId().toString()
      );
    }


  }

  @Override
  public void prepareSynchronization(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    scraperBase.syncSynchronization(
        work,
        dto.getId().toString(),
        SynchronizationOriginType.MEDIOCRESCAN,
        dto.getSlug()
    );
  }

  @Override
  public void prepareSyncSynopses(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncSynopses] ({}) Syncing synopses", dto.getNome());

    List<SynchronizationSynopses> synopses = new ArrayList<>();
    synopses.add(SynchronizationSynopses.builder()
        .language(UNKNOWN_LANGUAGE) // The site doesn't have a standard.
        .description(dto.getDescricao())
        .origin(SynchronizationOriginType.MEDIOCRESCAN)
        .build()
    );

    scraperBase.syncSynopses(
        work,
        synopses);
  }

  @Override
  public void prepareSyncTags(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work, VALIDATION_ERROR_WORK_IS_NULL);
    Objects.requireNonNull(dto, VALIDATION_ERROR_EXTERNAL_WORK_IS_NULL);

    log.debug("--> [MediocrescanService][syncTags] ({}) Syncing tags", dto.getNome());
    var tags = new ArrayList<SynchronizationTags>();
    if (dto.getTags() != null) {
      dto.getTags().forEach(tag -> tags.add(
          SynchronizationTags.builder()
              .group(TagGroupType.GENRE)
              .name(tag.getNome())
              .build()
      ));
    }
    scraperBase.syncTags(work, tags);
  }

  @Override
  public void prepareSyncCover(Work work, Mediocrescan_ObraDTO dto) throws IOException, InterruptedException {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncCover] ({}) Syncing cover", dto.getNome());
    try {
      scraperBase.syncCover(
          work,
          mediocreScanUrlAPI + "/storage/obras/" + dto.getId() + "/" + dto.getImagem(),
          dto.getImagem(),
          SynchronizationOriginType.MEDIOCRESCAN,
          CoverType.HIGH
      );

    } catch (Exception e) {
      log.error("[MediocrescanService][syncCover] Error syncing cover for work {} from MediocreScan", dto.getNome(), e);
      throw e;
    }
  }

  @Override
  public void prepareSyncLinks(Work work, Mediocrescan_ObraDTO workDto) {
    log.debug("--> [MediocrescanService][prepareSyncLinks] ({}) Syncing links", workDto.getNome());
  }

  @Override
  public void prepareSyncAuthors(Work work, Mediocrescan_ObraDTO workDto) {
    log.debug("--> [MediocrescanService][prepareSyncAuthors] ({}) Syncing authors", workDto.getNome());
  }

  @Transactional
  public void prepareSyncChapters(Work work, Mediocrescan_ObraDTO dto) {
    if (dto.getTotalCapitulos() == null || dto.getTotalCapitulos() == 0) {
      return;
    }

    var lang = dto.getFormato().getNome().equalsIgnoreCase("ENGLISH") ? "en" : "pt-BR";
    var language = languageService.findOrCreate(lang, SynchronizationOriginType.MEDIOCRESCAN);
    var scanlator = scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN).get();

    var perPage = 100;
    var totalPages = 1;
    for (int i = 1; i <= totalPages; i++) {
      var chapters = mediocrescanClient.listarCapitulos(
          getToken(),
          dto.getId(),
          i,
          perPage,
          Direction.DESC.name().toLowerCase()
      );

      if (chapters.getData() == null || chapters.getData().isEmpty() || chapters.getPagination() == null) {
        continue;
      }
      totalPages = chapters.getPagination().getTotalPages(); // Update the total number of pages for the loop.

      chapters.getData().forEach(chapterDto -> {
        log.info("--> [MediocrescanService][syncChapters] ({}) Syncing chapter {}",
            dto.getNome(),
            chapterDto.getNumero()
        );

        if (work.getChapters() == null) {
          work.setChapters(new ArrayList<>());
        }

        var chapter =
            chapterService.findByNumberAndWorkIdAndScanlatorId(chapterDto.getNumeroWithScale(), work, scanlator,
                    language)
                .orElseGet(() -> null);

        var toNotifyNew = false;
        if (chapter == null) {
          var volumeName = chapterDto.getVolume() != null ? chapterDto.getVolume() : null;

          BigDecimal decimal = chapterDto.getNumero().remainder(BigDecimal.ONE);

          var version = decimal.scale() > 0
              ? decimal.movePointRight(decimal.scale()).abs().toPlainString()
              : "0";

          chapter = chapterService.save(Chapter.builder()
              .work(work)
              .number(chapterDto.getNumeroWithScale())
              .numberFormatted(StringUtils.completeWithZeroZeroToLeft(
                  chapterDto.getNumero().setScale(0, RoundingMode.FLOOR).toPlainString(), 4)
              )
              .version("v" + StringUtils.completeWithZeroZeroToLeft(version, 2))
              .scanlator(scanlator)
              .language(language)
              .synced(false)
              .volume(volumeName == null ? null : volumeService.findOrCreate(work, volumeName, null))
              .createdAt(OffsetDateTime.now())
              .disabled(false)
              .build()
          );
          toNotifyNew = true;
        }

        /*
         * Checking if the chapter has been updated.
         */
        chapter.setSynced(false);
        if (chapter.getCreatedAt() != null
            && chapter.getPublishedAt() != null
            && (chapter.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)
            .equals(chapterDto.getCriadoEm().truncatedTo(ChronoUnit.SECONDS))
            && chapter.getPublishedAt().truncatedTo(ChronoUnit.SECONDS)
            .equals(chapterDto.getLancadoEm().truncatedTo(ChronoUnit.SECONDS)))) {
          chapter.setSynced(true);
        }

        if (Boolean.TRUE.equals(chapter.getSynced())) {
          log.info("--> [MediocrescanService][syncChapters] ({}) Chapter {} already synchronized", dto.getNome(),
              chapterDto.getNumero());
          return;
        }

        try {
          syncPage(work, dto, chapter, chapterDto);

          chapter.setPublishedAt(chapterDto.getLancadoEm().truncatedTo(ChronoUnit.SECONDS));
          chapter.setCreatedAt(chapterDto.getCriadoEm().truncatedTo(ChronoUnit.SECONDS));
          chapter.setSynced(true);
          chapterService.save(chapter);
          chapterNotifyService.save(ChapterNotify.builder()
              .chapter(chapter)
              .work(work)
              .status(toNotifyNew ? ChapterNotifyType.NEW : ChapterNotifyType.UPDATED)
              .createdAt(OffsetDateTime.now())
              .build());
        } catch (Exception e) {
          log.error("[MediocrescanService][syncChapters] ({}) Error syncing chapter {}", dto.getNome(),
              chapterDto.getNumero(), e);

          chapter.setSynced(false);
          chapter.setPublishedAt(null);
          chapterService.save(chapter);
          chapterNotifyService.save(ChapterNotify.builder()
              .chapter(chapter)
              .work(work)
              .status(ChapterNotifyType.ERROR)
              .createdAt(OffsetDateTime.now())
              .build());

          scraperBase.syncWorkError(
              SynchronizationOriginType.MEDIOCRESCAN,
              work != null && work.getId() == null ? null : Objects.requireNonNull(work).getId().toString(),
              dto.getId().toString(),
              "(" + dto.getFormato().getNome() + ") " + dto.getNome(),
              e.getMessage(),
              Arrays.toString(e.getStackTrace())
          );
        }
        //Garante que nao sera duplicado em caso de uma re-sincronizacao
        work.getChapters().remove(chapter);
        work.getChapters().add(chapter);
      });
    }
  }

  @Transactional
  protected void syncPage(Work work, Mediocrescan_ObraDTO dto, Chapter chapter, Mediocrescan_CapituloDTO chapterDto) {

    if (Boolean.FALSE.equals(chapterDto.getTemPaginas())) {
      syncPageNovel(work, dto, chapter, chapterDto);
      return;
    }

    log.info("--> X <-- [MediocrescanService][syncPage] ({}) Syncing chapter {}", dto.getNome(),
        chapterDto.getNumero());
    var pageDto = mediocrescanClient.obterCapitulo(getToken(), chapterDto.getId());

    // Verifica se a quantidade de paginas é diferente
    var pageCount = pageService.countByChapterNumber(chapter);

    // Pool dedicado para este capítulo - permite cancelar apenas os downloads deste capítulo
    ExecutorService chapterExecutor = Executors.newFixedThreadPool(5);
    var cancelled = new AtomicBoolean(false);
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    try {
      for (var p = 0; p < pageDto.getPaginas().size(); p++) {
        final int pageIndex = p;

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
          if (cancelled.get()) return;

          var page = pageService.findByNumberNotDisabled(chapter, pageIndex + 1);
          if (page == null || page.getDisabled()) {
            page = Page.builder()
                .chapter(chapter)
                .pageNumber(pageIndex + 1)
                .type(PageType.IMAGE)
                .content(null)
                .disabled(false)
                .build();
          }

          page.setFileName(
              StringUtils.completeWithZeroZeroToLeft(page.getPageNumber().toString(), 4)
                  + "__" + pageDto.getPaginas().get(pageIndex).getSrc()
          );

          var fileSrc = mediocreScanUrlCDN
              + "/obras/" + dto.getId()
              + "/capitulos/" + chapterDto.getNumero()
              + "/" + pageDto.getPaginas().get(pageIndex).getSrc();

          var path = work.getPublicationDemographic().name().toLowerCase()
              + "/" + work.getSlug()
              + "/" + "chapters"
              + "/" + chapter.getNumberFormatted()
              + "/" + chapter.getScanlator().getCode().toLowerCase()
              + "/" + chapter.getLanguage().getCode().toLowerCase()
              + "/" + chapter.getVersion();

          try {
            externalFileService.downloadExternalPublicObjectAndUploadToStorage(
                fileSrc,
                page.getFileName(),
                path
            );

            pageService.save(page);
          } catch (Exception e) {
            cancelled.set(true);
            log.error("--> [MediocrescanService][syncPage] ({}) Error downloading file: {}",
                chapterDto.getObra().getObraNome(),
                fileSrc, e);
            throw new RuntimeException("Error downloading page", e);
          }
        }, chapterExecutor);

        futures.add(future);
      }

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    } catch (CompletionException e) {
      cancelled.set(true);
      futures.forEach(f -> f.cancel(true));
      chapterExecutor.shutdownNow();
      throw new RuntimeException("Error downloading pages for chapter " + chapterDto.getNumero(), e.getCause());
    } finally {
      chapterExecutor.shutdown();
    }

  }

  @Transactional
  protected void syncPageNovel(Work work, Mediocrescan_ObraDTO dto, Chapter chapter,
                               Mediocrescan_CapituloDTO chapterDto) {

    if (Boolean.TRUE.equals(chapterDto.getTemPaginas())) {
      return;
    }

    var pageCount = pageService.countByChapterNumber(chapter);
    if (pageCount == dto.getTotalCapitulos()) {
      log.info("--> [MediocrescanService][syncPageNovel] ({}) All pages already synchronized for chapter {}",
          dto.getNome(), chapterDto.getNumero());
      return;
    }

    var page = pageService.findByNumberNotDisabled(chapter, 1);
    if (page != null) {
      log.debug("--> [MediocrescanService][syncPageNovel] ({}) Page found for chapter: {}, not need sync",
          chapterDto.getObra().getObraNome(), chapterDto.getNumero());
      return;
    }

    log.info("--> X <-- [MediocrescanService][syncPageNovel] ({}) Syncing chapter {}", dto.getNome(),
        chapterDto.getNumero());
    var pageDto = mediocrescanClient.obterCapitulo(getToken(), chapterDto.getId());

    if (chapterDto.getTipo() != null
        && chapterDto.getTipo().equalsIgnoreCase("markdown")
        && pageDto.getConteudoTexto() != null
        && !pageDto.getConteudoTexto().isEmpty()) {
      var pageNovel = Page.builder()
          .chapter(chapter)
          .pageNumber(1)
          .type(PageType.MARKDOWN)
          .content(pageDto.getConteudoTexto())
          .disabled(false)
          .build();
      pageService.save(pageNovel);
    }
  }
}
