package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_PaginacaoRecenteDTO {
  
  private Integer total;
  private Integer paginas;
  @JsonProperty("pagina_atual")
  private Integer paginaAtual;
  @JsonProperty("itens_por_pagina")
  private Integer itensPorPagina;
}