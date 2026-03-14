package dev.williancorrea.manhwa.reader.scraper.mangotoons.client;

import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.dto.Mango_ObraCollectionDTO;
import dev.williancorrea.manhwa.reader.scraper.mangotoons.dto.Mango_ObraDetalheDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "mangotoons-client",
    url = "${synchronization.mangotoons.api.url}",
    configuration = FeignConfig.class
)
public interface MangoClient {

  // No encryption
  @GetMapping("/api/obras")
  Mango_ObraCollectionDTO findAll(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "limite", defaultValue = "20") Integer limite,
      @RequestParam(value = "pagina", defaultValue = "1") Integer pagina,
      @RequestParam(value = "formato_id", required = false) String formato_id,
      @RequestParam(value = "formato", required = false) String formato,
      @RequestParam(value = "busca", required = false) String titulo
  );

  // No encryption
  @GetMapping("/api/obras/{id}")
  Mango_ObraDetalheDTO findById(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable Long id
  );


}