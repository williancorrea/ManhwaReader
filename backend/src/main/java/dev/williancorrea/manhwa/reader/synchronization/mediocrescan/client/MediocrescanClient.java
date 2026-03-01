package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.client;

import dev.williancorrea.manhwa.reader.config.FeignConfig;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapitulosResponse;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_LoginDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_RefreshTokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_TokenDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login.Mediocrescan_TokenNewDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObrasResponse;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.pagina.Mediocrescan_PaginaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "mediocrescan-client",
    url = "${synchronization.mediocrescan.api.url}",
    configuration = FeignConfig.class
)
public interface MediocrescanClient {

  @PostMapping("/auth/login")
  Mediocrescan_TokenDTO login(@RequestBody Mediocrescan_LoginDTO login);

  @PostMapping("/auth/refresh")
  Mediocrescan_TokenNewDTO refreshToken(@RequestBody Mediocrescan_RefreshTokenDTO refreshToken);

  @GetMapping("/obras")
  Mediocrescan_ObrasResponse listarObras(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "limite") Integer limite,
      @RequestParam(value = "pagina") Integer pagina,
      @RequestParam(value = "ordenarPor", required = false) String ordenarPor,
      @RequestParam(value = "formato", required = false) String formato
  );

  @GetMapping("/capitulos")
  Mediocrescan_CapitulosResponse listarCapitulos(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "obr_id") Long id,
      @RequestParam(value = "page") Integer page,
      @RequestParam(value = "limite") Integer perPage,
      @RequestParam(value = "order") String order
  );

  @GetMapping("/capitulos/{id}")
  Mediocrescan_PaginaResponse obterCapitulo(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable("id") Long id
  );


//  @GetMapping("/obra/{id}")
//  Mediocrescan_ObraDetalhadaResponse obterObra(
//      @RequestHeader("Authorization") String bearerToken,
//      @PathVariable("id") Long id
//  );
//
//  @GetMapping("/recentes")
//  Mediocrescan_RecentesResponse listarRecentes(
//      @RequestHeader("Authorization") String bearerToken,
//      @RequestParam(value = "page", required = false) Integer page,
//      @RequestParam(value = "per_page", required = false) Integer perPage
//  );
}