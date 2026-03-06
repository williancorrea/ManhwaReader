package dev.williancorrea.manhwa.reader.synchronization.mediocrescan;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import dev.williancorrea.manhwa.reader.features.scanlator.error.ScanlatorSynchronizationErrorService;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.tag.TagService;
import dev.williancorrea.manhwa.reader.features.volume.VolumeService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkTag;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLinkRepository;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.synchronization.base.Synchronization;
import dev.williancorrea.manhwa.reader.synchronization.base.SynchronizationBase;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationSynopses;
import dev.williancorrea.manhwa.reader.synchronization.base.input.SynchronizationTitle;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.client.MediocrescanClient;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapituloDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_LoginDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_RefreshTokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_TokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraDTO;
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
public class MediocrescanService extends SynchronizationBase implements Synchronization<Mediocrescan_ObraDTO> {

  public final MediocrescanClient mediocrescanClient;

  public final WorkService workService;
  public final LanguageService languageService;
  public final ScanlatorSynchronizationErrorService scanlatorSynchronizationErrorService;
  public final ScanlatorService scanlatorService;
  public final WorkLinkRepository workLinkRepository;


  public final TagService tagService;
  public final ExternalFileService externalFileService;
  public final ChapterService chapterService;

  public final PageService pageService;
  public final VolumeService volumeService;
  public final ChapterNotifyService chapterNotifyService;


  public MediocrescanService(@Lazy WorkService workService,
                             @Lazy LanguageService languageService,
                             @Lazy ScanlatorSynchronizationErrorService scanlatorSynchronizationErrorService,
                             @Lazy ScanlatorService scanlatorService,
                             @Lazy WorkLinkRepository workLinkRepository,

                             @Lazy MediocrescanClient mediocrescanClient,
                             @Lazy TagService tagService,
                             @Lazy ExternalFileService externalFileService,
                             @Lazy ChapterService chapterService,
                             @Lazy PageService pageService,
                             @Lazy VolumeService volumeService,
                             @Lazy ChapterNotifyService chapterNotifyService
  ) {
    super(workService,
        languageService,
        scanlatorSynchronizationErrorService,
        scanlatorService,
        workLinkRepository
    );

    this.workService = workService;
    this.languageService = languageService;
    this.scanlatorSynchronizationErrorService = scanlatorSynchronizationErrorService;
    this.scanlatorService = scanlatorService;
    this.workLinkRepository = workLinkRepository;

    this.mediocrescanClient = mediocrescanClient;
    this.tagService = tagService;
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

  private String token = null;

  private String getToken() {
    Mediocrescan_TokenDTO access = null;
    if (token == null) {
      log.debug("--> [MediocrescanService] Getting token");
      access = mediocrescanClient.login(new Mediocrescan_LoginDTO(mediocrescanApiUsername, mediocrescanApiPassword));
    }
    Objects.requireNonNull(access);
    log.debug("--> [MediocrescanService] Refreshing token");
    return "Bearer " + mediocrescanClient.refreshToken(
        new Mediocrescan_RefreshTokenDTO(access.getRefreshToken())
    ).getAccessToken();
  }

  @PostConstruct
  @Transactional
  @Override
  public void ScheduledSynchronization() {
    log.info("--> [MediocrescanService][ScheduledSynchronization] Starting synchronization with Mediocrescan");

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
      10- HENTAI
       */

//      var titulo = "Cavaleiro em eterna regressão"; //COMIC
//      var titulo = "Reencarnei no Corpo de um Príncipe Canalha"; //COMIC
//      var titulo = "I Became a Munchkin Skill Thief"; // ENGLISH
      var titulo = "Irmãs Ki"; // ENGLISH - 1 Caps
//      var titulo = "Necromante! Eu Sou Um Desastre"; //COMIC e NOVEL
//      var titulo = "O Gênio Que Lê O Mundo"; // COMIC - Testando titulos alternativos

      var obras = mediocrescanClient.listarObras(
          getToken(),
          1, //Padrao 24
          i + 1,
          "data_ultimo_cap",
          "1,5",  // 1,3,4
          titulo
      );

      totalPages = obras.getPagination().getTotalPages();
      obras.getData().forEach(this::synchronizeByExternalId);
      this.sleep(5000);
    }
  }

  @Transactional
  @Override
  public void synchronizeByWork(Work work) {
    log.info("--> [MediocrescanService][synchronizeByWork] Starting synchronization with Mediocrescan for work: {}",
        work.getTitles().getFirst().getTitle());
  }

  @Transactional
  @Override
  public void synchronizeByExternalId(Mediocrescan_ObraDTO obra) {
    log.info(
        "--> [MediocrescanService][synchronizeByExternalId] Starting synchronization with Mediocrescan for obra: {} - {}",
        obra.getId(), obra.getNome());

    Work work = null;
    try {
      work = this.findWorkOrCreate(obra.getId().toString(), SynchronizationOriginType.MEDIOCRESCAN);

      prepareSyncTitle(work, obra);
      prepareSyncAttributes(work, obra);

      prepareSynchronization(work, obra);
      prepareSyncSynopses(work, obra);
      syncTags(work, obra);
      syncCover(work, obra);

      work.setUpdatedAt(OffsetDateTime.now());
      work = workService.save(work);

//      syncRelationship(work, obra);
//      syncChapters(work, obra);


      log.info("<-- [MediocrescanService][synchronizeByExternalId] Synchronization completed: {}",
          obra.getNome().trim());
    } catch (Exception e) {
      this.syncWorkError(
          SynchronizationOriginType.MEDIOCRESCAN,
          work != null && work.getId() == null ? null : Objects.requireNonNull(work).getId().toString(),
          obra.getId().toString(),
          obra.getNome(),
          e.getMessage()
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
    this.syncTitle(work, titles);
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

    syncAttributes(
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

  private void syncRelationship(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getRelationship() == null) {
      if (dto.getObraNovel() != null) {
        var rel = workService.findBySynchronizationExternalID(
                dto.getObraNovel().getId().toString(),
                SynchronizationOriginType.MEDIOCRESCAN)
            .orElse(null);
        if (rel != null) {
          work.setRelationship(rel);
          rel.setRelationship(work);
          workService.save(rel);
          workService.save(work);
        }
      } else if (dto.getObraOriginal() != null) {
        var rel = workService.findBySynchronizationExternalID(
                dto.getObraOriginal().getId().toString(),
                SynchronizationOriginType.MEDIOCRESCAN)
            .orElse(null);
        if (rel != null) {
          work.setRelationship(rel);
          rel.setRelationship(work);
        }
      }
    }
  }

  @Override
  public void prepareSynchronization(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    this.syncSynchronization(
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
        .language("xx-XX") // The site doesn't have a standard.
        .description(dto.getDescricao())
        .origin(SynchronizationOriginType.MEDIOCRESCAN)
        .build()
    );

    this.syncSynopses(
        work,
        synopses);
  }

  private void syncTags(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getTags() == null) {
      work.setTags(new ArrayList<>());
    }

    log.debug("--> [MediocrescanService][syncTags] ({}) Syncing tags", dto.getNome());
    if (dto.getTags() != null && !dto.getTags().isEmpty()) {
      dto.getTags().forEach(tag -> {
        var group = TagGroupType.GENRE;
        var name = tag.getNome();

        if (!work.getTagsContains(group, name) && !name.isBlank()) {
          work.getTags().add(
              WorkTag.builder()
                  .tag(tagService.findOrCreate(group, name))
                  .work(work)
                  .build());
        }
      });
    }
  }

  private void syncCover(Work work, Mediocrescan_ObraDTO dto) throws IOException, InterruptedException {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncCover] ({}) Syncing cover", dto.getNome());
    if (work.getCoverCustom() == null || work.getCoverCustom().isEmpty()) {
      var coverUrl = mediocreScanUrlAPI + "/storage/obras/" + dto.getId() + "/" + dto.getImagem();
      work.setCoverCustom("cover_custom." + dto.getImagem().split("\\.")[1]);

      externalFileService.downloadWithAuthAndUpload(
          coverUrl,
          work.getCoverCustom(),
          work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug()
      );
    }
  }

  @Transactional
  protected void syncChapters(Work work, Mediocrescan_ObraDTO dto) {
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

        if (Boolean.TRUE.equals(chapter.getSynced())) {
          log.info("--> [MediocrescanService][syncChapters] ({}) Chapter {} already synchronized", dto.getNome(),
              chapterDto.getNumero());
          return;
        }

        try {
          syncPage(work, dto, chapter, chapterDto);

          chapter.setSynced(true);
          chapterService.save(chapter);
          chapterNotifyService.save(ChapterNotify.builder()
              .chapter(chapter)
              .status(toNotifyNew ? ChapterNotifyType.NEW : ChapterNotifyType.UPDATED)
              .build());
        } catch (Exception e) {
          log.error("[MediocrescanService][syncChapters] ({}) Error syncing chapter {}", dto.getNome(),
              chapterDto.getNumero(), e);

          chapter.setSynced(false);
          chapterService.save(chapter);
          chapterNotifyService.save(ChapterNotify.builder()
              .chapter(chapter)
              .status(ChapterNotifyType.ERROR)
              .build());
        }
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

    if (pageCount == pageDto.getPaginas().size()) {
      log.info("--> [MediocrescanService][syncPage] ({}) Page count match for chapter {}",
          dto.getNome(),
          chapterDto.getNumero());
      return;
    }
    log.info("--> [MediocrescanService][syncPage] ({}) Page count mismatch for chapter {}",
        dto.getNome(),
        chapterDto.getNumero());

    for (var p = 0; p < pageDto.getPaginas().size(); p++) {
      var page = pageService.findByNumberNotDisabled(chapter, p + 1);
      if (page == null || page.getDisabled()) {
        page = Page.builder()
            .chapter(chapter)
            .pageNumber(p + 1)
            .type(PageType.IMAGE)
            .content(null)
            .disabled(false)
            .build();
      }

      page.setFileName(
          StringUtils.completeWithZeroZeroToLeft(page.getPageNumber().toString(), 4)
              + "__" + pageDto.getPaginas().get(p).getSrc()
      );

      var fileSrc = mediocreScanUrlCDN
          + "/obras/" + dto.getId()
          + "/capitulos/" + chapterDto.getNumero()
          + "/" + pageDto.getPaginas().get(p).getSrc();

      var path = work.getPublicationDemographic().name().toLowerCase()
          + "/" + work.getSlug()
          + "/chapters"
          + "/" + chapter.getNumberFormatted()
          + "/" + chapter.getScanlator().getCode().toLowerCase()
          + "/" + chapter.getLanguage().getCode().toLowerCase()
          + "/" + chapter.getVersion();

      try {
        externalFileService.downloadWithAuthAndUpload(
            fileSrc,
            page.getFileName(),
            path
        );

        pageService.save(page);
      } catch (Exception e) {
        log.error("--> [MediocrescanService][syncPage] ({}) Error downloading file: {}",
            chapterDto.getObra().getObraNome(),
            fileSrc, e);
      }
    }

  }

  @Override
  public void prepareSyncLinks(Work work, Mediocrescan_ObraDTO workDto) {
    log.debug("--> [MediocrescanService][prepareSyncLinks] ({}) Syncing links", workDto.getNome());
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
