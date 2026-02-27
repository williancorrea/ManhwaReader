package dev.williancorrea.manhwa.reader.synchronization.lycantoons;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.language.LanguageService;
import dev.williancorrea.manhwa.reader.features.scanlator.ScanlatorService;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LycantoonsService {
  public final WorkService workService;
  public final ScanlatorService scanlatorService;
  public final LanguageService languageService;

  private static final String CDN_LYCANTOONS = "https://cdn.lycantoons.com/file/lycantoons";

//  @PostConstruct
  public void init() {

    var work = workService.findAll().getFirst();
    if (!work.getSynchronizationsContains(SynchronizationOriginType.LYCANTOONS)) {
      work.getSynchronizations().add(WorkSynchronization.builder()
          .origin(SynchronizationOriginType.LYCANTOONS)
          .externalId("o-cavaleiro-em-eternidade-regredida")
          .work(work)
          .build());
      work = workService.save(work);
    }
    start(work);
  }

  public void start(Work work) {
    log.info("--> LycantoonsService initialized");
    Objects.requireNonNull(work);

    log.info("--> Starting Lycantoons synchronization for work: {}", work.getTitles().getFirst().getTitle());

    if (work.getChapters() == null) {
      work.setChapters(new ArrayList<>());
    }
    var lastChapter = scanlatorService.getLastChapterNumber(work).orElseGet(() -> BigDecimal.ZERO);

    var scanlator = Objects.requireNonNull(
        scanlatorService.findBySynchronization(SynchronizationOriginType.LYCANTOONS).get()
    );

    var language = languageService.findOrCreate("pt-BR", SynchronizationOriginType.LYCANTOONS);
    var chapter = Chapter.builder()
        .work(work)
        .number(lastChapter)
        .title("Chapter " + lastChapter)
        .language(language)
        .scanlator(scanlator)
        .build();


    var scanChapterFolder = work.getSynchronizations()
        .stream()
        .filter(s -> s.getOrigin().equals(SynchronizationOriginType.LYCANTOONS))
        .findFirst().get().getExternalId();

    var prefixScanCap = "cap-";
    var prefixScanPage = "page-";
    var scanPage = 1;

    var url = CDN_LYCANTOONS
        + "/" + scanChapterFolder + "/"
        + prefixScanCap + lastChapter
        + "/" + prefixScanPage + StringUtils.completeWithZeroZeroToLeft(String.valueOf(scanPage), 4) + ".jpg";

    log.info("URL1: {}", url);
    log.info(
        "URL2: https://cdn.lycantoons.com/file/lycantoons/o-cavaleiro-em-eternidade-regredida/cap-0/page-0001.jpg");


    var workFolder = work.getSlug();
    var scanFolder = scanlator.getCode().toLowerCase();

    var urlPage = workFolder
        + "/" + StringUtils.completeWithZeroZeroToLeft(String.valueOf(lastChapter), 4)
        + "/" + scanFolder
        + "/" + StringUtils.completeWithZeroZeroToLeft(String.valueOf(scanPage), 4) + ".jpg";

    log.info("URL3: {}", urlPage);


//    https://cdn.lycantoons.com/file/lycantoons/o-cavaleiro-em-eternidade-regredida/cap-0/page-0001.jpg


    //Pegar o ultimo capitulo importado    
    //Pegar a ultima pagina importada

    // Verificar o capitulo 0 se nao tiver pesquisar pelo 1

    //pegar o nome do bucket
    // criar a pasta do capitulo dentro do obra
    // salvar a imagem dentro do capitulo
    //Remover a tabela de scanlator


//    IMPORTANTE
//    SALVAR NO SEQUINTE PADRAO - 
//        oneul_man_saneun_gisa/0001/MD/0001.jpg
//        oneul_man_saneun_gisa/0001/LY/0001.jpg


    //    Por fim verificar se tem como pegar a capa

    //    MangaDexResponse response = lycantoonsClient.getPage(title, cap, page);

//    Volume(Opcional)
//    Capitulo
//    Pagina

//    AO TERMINAR, GRAVAR A NOTIFICACAO DE CAPITULO NOVO

    log.info("<-- Lycantoons synchronization completed for work: {}", work.getTitles().getFirst().getTitle());
  }
}
