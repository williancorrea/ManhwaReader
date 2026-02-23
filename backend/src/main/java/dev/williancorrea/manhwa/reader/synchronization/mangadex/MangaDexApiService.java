package dev.williancorrea.manhwa.reader.synchronization.mangadex;

import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkService;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.client.MangaDexClient;
import dev.williancorrea.manhwa.reader.synchronization.mangadex.dto.MangaDexResponse;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
//@RequiredArgsConstructor
public class MangaDexApiService {

  private final MangaDexClient mangaDexClient;
  private final MangaDexMapperService mapperService;
  private final WorkService workService;

  public MangaDexApiService(MangaDexClient mangaDexClient,
                            MangaDexMapperService mapperService,
                            WorkService workService) {
    this.mangaDexClient = mangaDexClient;
    this.mapperService = mapperService;
    this.workService = workService;
  }

  //  //TODO: REMOVER
  @PostConstruct
  public void init() {
    searchMangaFromExternalApi("Oneul man Saneun Gisa", Pageable.ofSize(5));
  }

  @Transactional
  public MangaDexResponse searchMangaFromExternalApi(String title, Pageable pageable) {
    try {

      MangaDexResponse response = mangaDexClient.searchManga(
          title,
          pageable.getPageNumber(),
          pageable.getPageSize(),
          new String[] {"author", "artist", "cover_art"}
      );

      if ("ok" .equals(response.getResult()) && response.getData() != null) {
        response.getData().forEach(mangaData -> {
          Work work = mapperService.toEntity(mangaData);
          workService.save(work);
        });

      }
      return response;
    } catch (FeignException e) {
      log.error("Error searching manga from MangaDex API: {}", e.getMessage());
      //TODO: Implementar um tratamento mais adequado de exceções
      throw new RuntimeException("Failed to search manga from MangaDex API", e);
    }
  }

  //  public MangaDexResponse fetchMangaFromExternalApi(Integer limit, Integer offset) {
//    try {
//      Map<String, Object> queryParams = new HashMap<>();
//      queryParams.put("limit", limit);
//      queryParams.put("offset", offset);
//      queryParams.put("includes[]", new String[] {"author", "artist", "cover_art"});
//      queryParams.put("order[latestUploadedChapter]", "desc");
//
//      MangaDexResponse response = mangaDexClient.getMangaList(queryParams);
//
//      // Salvar no banco de dados
//      if ("ok".equals(response.getResult()) && response.getData() != null) {
//        response.getData().forEach(mangaData -> {
//          
//          Manga manga = mapperService.toEntity(mangaData);
//          mangaRepository.save(manga);
//          
//        });
//      }
//
//      return response;
//
//    } catch (FeignException e) {
//      log.error("Error fetching manga from MangaDex API: {}", e.getMessage());
//      throw new RuntimeException("Failed to fetch data from MangaDex API", e);
//    }
//  }

//  public MangaDexResponse fetchMangaByIdFromExternalApi(String id) {
//    try {
//      Map<String, Object> includes = Map.of("includes[]", new String[] {"author", "artist", "cover_art"});
//
//      MangaDexResponse response = mangaDexClient.getMangaById(id, includes);
//
//      // Salvar no banco de dados
//      if ("ok".equals(response.getResult()) && response.getData() != null && !response.getData().isEmpty()) {
//        Manga manga = mapperService.toEntity(response.getData().get(0));
////        mangaRepository.save(manga);
//      }
//
//      return response;
//
//    } catch (FeignException e) {
//      log.error("Error fetching manga by ID from MangaDex API: {}", e.getMessage());
//      throw new RuntimeException("Failed to fetch manga from MangaDex API", e);
//    }
//  }
//
//  public MangaDexResponse fetchMangaByDemographicFromExternalApi(String demographic, Integer limit, Integer offset) {
//    try {
//      MangaDexResponse response = mangaDexClient.getMangaByDemographic(
//          demographic, limit, offset, new String[] {"author", "artist", "cover_art"}
//      );
//
//      // Salvar no banco de dados
//      if ("ok".equals(response.getResult()) && response.getData() != null) {
//        response.getData().forEach(mangaData -> {
////          Manga manga = mapperService.toEntity(mangaData);
////          mangaRepository.save(manga);
//        });
//      }
//
//      return response;
//
//    } catch (FeignException e) {
//      log.error("Error fetching manga by demographic from MangaDex API: {}", e.getMessage());
//      throw new RuntimeException("Failed to fetch manga by demographic from MangaDex API", e);
//    }
//  }
//
//  public MangaDexResponse syncMangaData(String id) {
//    // Primeiro tenta buscar do banco local
//    Optional<Manga> localManga = mangaRepository.findById(id);
//
//    if (localManga.isPresent()) {
//      // Verifica se os dados estão desatualizados (mais de 1 hora)
//            /* 
//            if (isDataStale(localManga.get().getEntityUpdatedAt())) {
//                return fetchMangaByIdFromExternalApi(id);
//            }
//            */
//
//      // Retorna dados locais
//      return convertLocalMangaToResponse(localManga.get());
//    } else {
//      // Busca da API externa
//      return fetchMangaByIdFromExternalApi(id);
//    }
//  }
//
//  private MangaDexResponse convertLocalMangaToResponse(Manga manga) {
//    // Implementar conversão de Manga entity para MangaDexResponse
//    // Similar ao que foi feito no MangaService anterior
//    MangaDexResponse response = new MangaDexResponse();
//    response.setResult("ok");
//    response.setResponse("entity");
//    // ... preencher com dados do manga
//    return response;
//  }
//
//  private boolean isDataStale(java.time.LocalDateTime lastUpdate) {
//    return lastUpdate.isBefore(java.time.LocalDateTime.now().minusHours(1));
//  }
//
//  public MangaDexResponse fetchMangaByContentRatingFromExternalApi(String contentRating, Integer limit,
//                                                                   Integer offset) {
//    try {
//      Map<String, Object> queryParams = new HashMap<>();
//      queryParams.put("contentRating[]", new String[] {contentRating});
//      queryParams.put("limit", limit);
//      queryParams.put("offset", offset);
//      queryParams.put("includes[]", new String[] {"author", "artist", "cover_art"});
//
//      MangaDexResponse response = mangaDexClient.getMangaList(queryParams);
//
//      // Salvar no banco de dados
//      if ("ok".equals(response.getResult()) && response.getData() != null) {
//        response.getData().forEach(mangaData -> {
//          Manga manga = mapperService.toEntity(mangaData);
//          mangaRepository.save(manga);
//        });
//      }
//
//      return response;
//
//    } catch (FeignException e) {
//      log.error("Error fetching manga by content rating from MangaDex API: {}", e.getMessage());
//      throw new RuntimeException("Failed to fetch manga by content rating from MangaDex API", e);
//    }
//  }
}