package dev.williancorrea.manhwa.reader.synchronization.mediocrescan;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.chapter.ChapterService;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.page.Page;
import dev.williancorrea.manhwa.reader.features.page.PageService;
import dev.williancorrea.manhwa.reader.features.page.PageType;
import dev.williancorrea.manhwa.reader.features.scanlator.ScanlatorService;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.tag.TagService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkPublicationDemographic;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.WorkStatus;
import dev.williancorrea.manhwa.reader.features.work.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.WorkTag;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;
import dev.williancorrea.manhwa.reader.features.work.WorkType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.minio.ExternalFileService;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.client.MediocrescanClient;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapituloDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_LoginDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_RefreshTokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_TokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraDTO;
import dev.williancorrea.manhwa.reader.utils.RemoveAccentuationUtils;
import dev.williancorrea.manhwa.reader.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediocrescanService {

  public final MediocrescanClient mediocrescanClient;
  public final WorkService workService;
  public final LanguageService languageService;
  public final TagService tagService;
  public final ExternalFileService externalFileService;
  public final ChapterService chapterService;
  public final ScanlatorService scanlatorService;
  public final PageService pageService;

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
  public void FindAllWorks() {
    //TODO: Montar o loop para buscar as proximas paginas (Verificar no site se tem que fazer a conta para o numero de pagina)
    log.warn("--> [MediocrescanService][FindAllWorks] Starting synchronization with Mediocrescan");
    var totalPages = 1;
    for (int i = 0; i < totalPages; i++) {
      log.warn("--> [MediocrescanService][FindAllWorks] External synchronization page {} of {}", i + 1, totalPages);
      
      
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
      var obras = mediocrescanClient.listarObras(
          getToken(),
          1, //Padrao 24
          i + 1,
          "data_ultimo_cap",
          "3");

      //TODO: DESCOMENTAR
//      totalPages = obras.getPagination().getTotalPages();
      obras.getData().forEach(this::synchronizeWork);
      waitForNextQuery();
    }
  }

  private static void waitForNextQuery() {
    try {
      log.debug("--> [MediocrescanService][waitForNextQuery] Sleeping for 5 seconds to next query (listarObras)");
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      log.error("Error sleeping for 5 seconds", e);
      Thread.currentThread().interrupt();
    }
  }


  @Transactional
  public void synchronizeWork(Mediocrescan_ObraDTO obra) {
    try {
      var work = findWork(obra);

      syncAttributes(work, obra);
      syncSynchronization(work, obra);
      syncTitle(work, obra);
      syncSynopses(work, obra);
      syncTags(work, obra);
      syncCover(work, obra);

      work.setUpdatedAt(OffsetDateTime.now());
      work = workService.save(work);

      syncRelationship(work, obra);
      syncChapters(work, obra);


      log.info("<-- [MediocrescanService][synchronizeWork] Synchronization completed: {}", obra.getNome().trim());
    } catch (Exception e) {
      //TODO: Notificar que deu ruim de alguma forma citar a obra quer deu ruim
      log.error("<-- [MediocrescanService][synchronizeWork] Error synchronizing with Mediocrescan: ({}) {} - {}",
          obra.getFormato().getNome().toUpperCase(),
          obra.getId(),
          obra.getNome(),
          e);
    }
  }

  private Work findWork(Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(dto);
    log.debug("--> [MediocrescanService][findWork] ({}) Finding work", dto.getNome());
    var work = workService.findBySynchronizationExternalID(dto.getId().toString()).orElse(null);
    if (work == null) {
      log.debug("--> [MediocrescanService][findWork] ({}) Work not found, creating new work", dto.getNome());
      work = workService.findByTitle(dto.getNome())
          .orElseGet(() -> Work.builder()
              .createdAt(OffsetDateTime.now())
              .disabled(false)
              .build());
    }
    return work;
  }

  private void syncRelationship(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getRelationship() == null) {
      if (dto.getObraNovel() != null) {
        var rel = workService.findBySynchronizationExternalID(dto.getObraNovel().getId().toString()).orElse(null);
        if (rel != null) {
          work.setRelationship(rel);
          rel.setRelationship(work);
          workService.save(rel);
          workService.save(work);

        }
      } else if (dto.getObraOriginal() != null) {
        var rel = workService.findBySynchronizationExternalID(dto.getObraOriginal().getId().toString()).orElse(null);
        if (rel != null) {
          work.setRelationship(rel);
          rel.setRelationship(work);
        }
      }
    }
  }

  private void syncSynchronization(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncSynchronization] ({}) Syncing work", dto.getNome());
    if (work.getSynchronizations() == null) {
      work.setSynchronizations(new ArrayList<>());
    }

    if (!work.getSynchronizationsContains(SynchronizationOriginType.MEDIOCRESCAN)) {
      work.getSynchronizations().add(
          WorkSynchronization.builder()
              .externalId(dto.getId().toString())
              .origin(SynchronizationOriginType.MEDIOCRESCAN)
              .work(work)
              .externalSlug(dto.getSlug())
              .build());
    }
  }

  private void syncTitle(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);


    if (work.getTitles() == null) {
      work.setTitles(new ArrayList<>());
    }

    log.debug("--> [MediocrescanService][syncTitle] ({}) Syncing title", dto.getNome());
    AtomicBoolean found = new AtomicBoolean(false);
    work.getTitles().forEach(obj -> {
      if (obj.getTitle().equalsIgnoreCase(dto.getNome().trim())) {
        found.set(true);
      }
    });

    if (!found.get()) {
      var lang = dto.getFormato().getNome().equalsIgnoreCase("ENGLISH") ? "en" : "pt-BR";
      work.getTitles().add(
          WorkTitle.builder()
              .work(work)
              .language(languageService.findOrCreate(lang, SynchronizationOriginType.MEDIOCRESCAN))
              .isOfficial(false)
              .title(dto.getNome().trim())
              .build()
      );
    }
  }

  private void syncAttributes(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService][syncAttributes] ({}) Syncing attributes", dto.getNome());
    if (work.getType() == null) {
      work.setType(
          dto.getFormato().getNome().toUpperCase().contains("NOVEL")
              ? WorkType.NOVEL
              : WorkType.MANHWA
      );
    }

    switch (dto.getStatus().getNome()) {
      case "Em Andamento", "Ativo":
        work.setStatus(WorkStatus.ONGOING);
        break;
      case "Concluído":
        work.setStatus(WorkStatus.COMPLETED);
        break;
      case "Hiato":
        work.setStatus(WorkStatus.HIATUS);
        break;
      case "Cancelada":
        work.setStatus(WorkStatus.CANCELLED);
        break;
      default:
        log.error("--> [MediocrescanService][syncAttributes] ({}) Unknown status: {}", dto.getNome(),
            dto.getStatus().getNome());
        throw new RuntimeException("Status not found: " + dto.getStatus().getNome());
    }

    if (work.getPublicationDemographic() == null) {
      var demographic = dto.getFormato().getNome().toUpperCase();
      if (demographic.isEmpty()) {
        demographic = WorkPublicationDemographic.UNKNOWN.name();
      }
      work.setPublicationDemographic(WorkPublicationDemographic.valueOf(demographic));
    }

    if (work.getSlug() == null || work.getSlug().isEmpty()) {
      work.setSlug(dto.getSlug());
    }
  }

  private void syncSynopses(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    if (work.getSynopses() == null) {
      work.setSynopses(new ArrayList<>());
    }
    log.debug("--> [MediocrescanService][syncSynopses] ({}) Syncing synopses", dto.getNome());
    var lang = dto.getFormato().getNome().equalsIgnoreCase("ENGLISH") ? "en" : "pt-BR";
    AtomicBoolean found = new AtomicBoolean(false);
    work.getSynopses().forEach(obj -> {
      if (obj.getLanguage().getCode().equalsIgnoreCase(lang)) {
        found.set(true);
      }
    });
    if (!found.get()) {
      work.getSynopses().add(
          WorkSynopsis.builder()
              .work(work)
              .language(languageService.findOrCreate(lang, SynchronizationOriginType.MEDIOCRESCAN))
              .description(dto.getDescricao())
              .build()
      );
    }

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
    if (work.getSlug() == null || work.getSlug().isEmpty()) {
      var title = work.getTitles()
          .stream()
          .map(WorkTitle::getTitle)
          .findFirst().orElse(
              "GENERATED" + UUID.randomUUID()
          );
      title = RemoveAccentuationUtils.normalize(title).toLowerCase();

      if (dto.getFormato().getNome().equalsIgnoreCase("NOVEL")) {
        title = title + "__novel";
      }
      work.setSlug(title);
    }

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
    var countChapter = chapterService.countByWorkIdAndScanlatorIdAndLanguageId(work, scanlator, language);

    if (Objects.equals(countChapter, dto.getTotalCapitulos())) {
      log.info("--> [MediocrescanService][syncChapters] ({}) All chapters already synchronized ", dto.getNome());
      return;
    }

    var perPage = 100;
    var totalPages = 1;
    for (int i = 1; i <= totalPages; i++) {
      var chapters = mediocrescanClient.listarCapitulos(
          getToken(),
          dto.getId(),
          i,
          perPage,
          Direction.ASC.name().toLowerCase()
      );

      if (chapters.getData() == null || chapters.getData().isEmpty() || chapters.getPagination() == null) {
        continue;
      }
      totalPages = chapters.getPagination().getTotalPages();


      chapters.getData().forEach(chapterDto -> {
        log.info("--> [MediocrescanService][syncChapters] ({}) Syncing chapter {}",
            dto.getNome(),
            chapterDto.getNumero()
        );


        var chapter =
            chapterService.findByNumberAndWorkIdAndScanlatorId(chapterDto.getNumero(), work, scanlator, language)
                .orElseGet(() -> null);

        if (chapter == null) {
          chapter = chapterService.save(Chapter.builder()
              .work(work)
              .number(BigDecimal.valueOf(chapterDto.getNumero()).setScale(2, RoundingMode.HALF_UP))
              .title(chapterDto.getDescricao())
              .scanlator(scanlator)
              .language(language)
              .createdAt(OffsetDateTime.now())
              .build()
          );
        }

        try {
          syncPage(work, dto, chapter, chapterDto);
        } catch (Exception e) {
          log.error("[MediocrescanService][syncChapters] ({}) Error syncing chapter {}", dto.getNome(),
              chapterDto.getNumero(), e);
          //TODO: Notificar que deu ruim de alguma forma citar a obra quer deu ruim e o capitulo
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

    var pageDto = mediocrescanClient.obterCapitulo(getToken(), chapterDto.getId());

    // Verifica se a quantidade de paginas é diferente
    var pageCount = pageService.countByChapterNumber(chapter);

    if (pageCount != pageDto.getPaginas().size()) {
      log.info("--> [MediocrescanService][syncPage] ({}) Page count mismatch for chapter {}",
          dto.getNome(),
          chapterDto.getNumero());

      AtomicInteger pageNumber = new AtomicInteger();
      pageDto.getPaginas().forEach(file -> {
        pageNumber.addAndGet(1);
        var page = pageService.findByNumberNotDisabled(chapter, pageDto.getNumero());
        if (page == null || page.getDisabled()) {
          page = Page.builder()
              .chapter(chapter)
              .pageNumber(pageNumber.get())
              .type(PageType.IMAGE)
              .content(null)
              .disabled(false)
              .build();
        }

        var toRemove = "";
        if (page.getFileName() != null && page.getFileName().equalsIgnoreCase(file.getSrc())) {
          toRemove = page.getFileName();
        }

        page.setFileName(
            StringUtils.completeWithZeroZeroToLeft(page.getPageNumber().toString(), 4) + "__" + file.getSrc()
        );

        var fileSrc = mediocreScanUrlCDN
            + "/obras/" + dto.getId()
            + "/capitulos/" + chapterDto.getNumero()
            + "/" + file.getSrc();

        try {
          externalFileService.downloadWithAuthAndUpload(
              fileSrc,
              page.getFileName(),
              work.getPublicationDemographic().name().toLowerCase()
                  + "/" + work.getSlug()
                  + "/" + chapter.getNumber()
                  + "/" + chapter.getScanlator().getCode().toLowerCase()
                  + "/" + chapter.getLanguage().getCode().toLowerCase()
          );

          pageService.save(page);

          //TODO: remover a antiga imagem, caso o sinc verifique que mudou
//        if (toRemove != null) {
//          minioService.deleteFile(work.getPublicationDemographic().name().toLowerCase() + "/" + work.getSlug() + "/" +
//              chapter.getScanlator().getCode().toLowerCase(), toRemove);
//        }
        } catch (Exception e) {
          log.error("--> [MediocrescanService][syncPage] ({}) Error downloading file: {}",
              chapterDto.getObra().getObraNome(),
              fileSrc, e);
        }
      });
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
