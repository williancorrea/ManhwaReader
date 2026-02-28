package dev.williancorrea.manhwa.reader.synchronization.mediocrescan;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.chapter.ChapterService;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
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
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_LoginDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_RefreshTokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_TokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraDTO;
import dev.williancorrea.manhwa.reader.utils.RemoveAccentuationUtils;
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
    log.debug("--> [MediocrescanService] Finding all works 0 to ?");

    //TODO: Montar o loop para buscar as proximas paginas (Verificar no site se tem que fazer a conta para o numero de pagina)
    log.warn("--> [MediocrescanService] Starting synchronization with Mediocrescan");
    var totalPages = 1;
    for (int i = 0; i < totalPages; i++) {
      log.warn("--> [MediocrescanService] Synchronizing page {} of {}", i + 1, totalPages);
      var obras = mediocrescanClient.listarObras(getToken(), 24, i + 1, "criada_em_desc");
//      totalPages = obras.getPagination().getTotalPages();
      obras.getData().forEach(this::synchronizeWork);
      waitForNextQuery();
    }
  }

  private static void waitForNextQuery() {
    try {
      log.debug("--> [MediocrescanService] Sleeping for 5 seconds to next query (listarObras)");
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

      syncChapters(work, obra);

      log.info("<-- [MediocrescanService] Synchronization completed: {}", obra.getNome().trim());
    } catch (Exception e) {
      //TODO: Notificar que deu ruim de alguma forma citar a obra quer deu ruim
      log.error("Error synchronizing with Mediocrescan: ({}) {} - {}",
          obra.getFormato().getNome().toUpperCase(),
          obra.getId(),
          obra.getNome(),
          e);
    }



    /*
    Fazer a busca Paginada das obras
      iterar cada obra
      validar a quantidade de paginas (para verificar se precisa sugar)
      atualizar o status da obra (Completado, Hiato, ...)
      Varificar o tipo da obra (Novel)
      Atualizar o formato da obra (manwha, shoujo)
      
      Verificar se teve alteração na obra atraves do (data_ultimo_cap)
      Verificar se teve alteração nos capitulos pela data de altualização
     */


    //Chamada da api contendo os nomes dos capitulos e a capa
//    https://api.mediocretoons.site/capitulos/237559?_t=1772108798684


//    https://cdn.mediocrescan.com/obras/2727/capitulos/222/5bc0a0045324d441ee0f0305f5e1b01cdad2f5a1.webp

//    https://api.mediocretoons.site/capitulos?obr_id=2727&page=1&limite=50&order=desc

//    Validar se é uma Novel

  }

  private Work findWork(Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(dto);
    log.debug("--> [MediocrescanService] Finding work: {}", dto.getNome());
    var work = workService.findBySynchronizationExternalID(dto.getId().toString()).orElse(null);
    if (work == null) {
      log.debug("--> [MediocrescanService] Work not found, creating new work: {}", dto.getNome());
      work = workService.findByTitle(dto.getNome())
          .orElseGet(() -> Work.builder()
              .createdAt(OffsetDateTime.now())
              .disabled(false)
              .build());
    }
    return work;
  }

  private void syncSynchronization(Work work, Mediocrescan_ObraDTO dto) {
    Objects.requireNonNull(work);
    Objects.requireNonNull(dto);

    log.debug("--> [MediocrescanService] Syncing work: {}", dto.getNome());
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

    log.debug("--> [MediocrescanService] Syncing title: {}", dto.getNome());
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

    log.debug("--> [MediocrescanService] Syncing attributes: {}", dto.getNome());
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
    log.debug("--> [MediocrescanService] Syncing synopses");
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

    log.debug("--> [MediocrescanService] Syncing tags");
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

    log.debug("--> [MediocrescanService] Syncing cover");
    if (work.getSlug() == null || work.getSlug().isEmpty()) {
      var title = work.getTitles()
          .stream().filter(title1 -> Boolean.TRUE.equals(title1.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findFirst().orElse("GENERATED" + UUID.randomUUID());
      title = RemoveAccentuationUtils.normalize(title).toLowerCase();
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
//  https://cdn.mediocrescan.com/storage/obras/2727/26edbe0b8c2e44d935aa25bc09cb8dd4ceca5d2f.webp
//  http://localhost:3000/storage/obras/2727/26edbe0b8c2e44d935aa25bc09cb8dd4ceca5d2f.webp  
//  https://api.mediocretoons.site/storage/obras/2727/26edbe0b8c2e44d935aa25bc09cb8dd4ceca5d2f.webp


//    dto.getRelationships().stream().filter(rel -> rel.getType().equals("cover_art")).findFirst().ifPresent(cover -> {
//      var extension = "." + cover.getAttributes().getFileName().split("\\.")[1];
//      try {
//        work.setCoverMedium("cover_512" + extension);
//        externalFileService.downloadWithAuthAndUpload(
//            MANDADEX_URL_COVERS + dto.getId() + "/" + cover.getAttributes().getFileName() + ".512.jpg",
//            "",
//            work.getCoverMedium(),
//            work.getSlug()
//        );
//
//        work.setCoverLow("cover_256" + extension);
//        externalFileService.downloadWithAuthAndUpload(
//            MANDADEX_URL_COVERS + dto.getId() + "/" + cover.getAttributes().getFileName() + ".256.jpg",
//            "",
//            work.getCoverLow(),
//            work.getSlug()
//        );
//
//        work.setCoverHigh("cover" + extension);
//        externalFileService.downloadWithAuthAndUpload(
//            MANDADEX_URL_COVERS + dto.getId() + "/" + cover.getAttributes().getFileName(),
//            "",
//            work.getCoverHigh(),
//            work.getSlug()
//        );
//
//      } catch (Exception e) {
//        throw new RuntimeException(e);
//      }
//    });
  }

  private void syncChapters(Work work, Mediocrescan_ObraDTO dto) {
    if (dto.getTotalCapitulos() == null || dto.getTotalCapitulos() == 0) {
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

      var scanlator = scanlatorService.findBySynchronization(SynchronizationOriginType.MEDIOCRESCAN).get();
      chapters.getData().forEach(chapterDto -> {
        log.info("--> [MediocrescanService][syncChapters] Syncing chapter {}", chapterDto.getNumero());

        var chapter = chapterService.findByNumberAndWorkIdAndScanlatorId(chapterDto.getNumero(), work, scanlator);
        var lang = dto.getFormato().getNome().equalsIgnoreCase("ENGLISH") ? "en" : "pt-BR";
        if (chapter.isEmpty()) {
          chapterService.save(Chapter.builder()
              .work(work)
              .number(BigDecimal.valueOf(chapterDto.getNumero()))
              .title(chapterDto.getDescricao())
              .scanlator(scanlator)
              .language(languageService.findOrCreate(lang, SynchronizationOriginType.MEDIOCRESCAN))
              .createdAt(OffsetDateTime.now())
              .build()
          );
        }
      });
    }
  }
  

}
