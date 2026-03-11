package dev.williancorrea.manhwa.reader.synchronization.mangotoons.client;

import dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto.Mango_FormatoColletcionDTO;
import dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto.Mango_ObraCollectionDTO;
import dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto.Mango_StatusColletcionDTO;
import dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto.Mango_TagColletcionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "mango-encrypted-client",
    url = "${synchronization.mangotoons.api.url}",
    configuration = MangoEncryptedFeignConfig.class
)
public interface MangoClientEncrypted {

  // Encrypted
  @GetMapping("/api/obras/recentes")
  Mango_ObraCollectionDTO findAllNew(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "limite") Integer limite,
      @RequestParam(value = "pagina") Integer pagina,
      @RequestParam(value = "formato_id", required = false) String ordenarPor,
      @RequestParam(value = "formato", required = false) String formato,
      @RequestParam(value = "busca", required = false) String titulo
  );
  
  // Encrypted
  @GetMapping("/api/filtros/formatos")
  Mango_FormatoColletcionDTO findByFormat(
      @RequestHeader("Authorization") String bearerToken
  );

  // Encrypted
  @GetMapping("/api/filtros/tags")
  Mango_TagColletcionDTO findByTag(
      @RequestHeader("Authorization") String bearerToken
  );

  // Encrypted
  @GetMapping("/api/filtros/status")
  Mango_StatusColletcionDTO findByStatus(
      @RequestHeader("Authorization") String bearerToken
  );

}