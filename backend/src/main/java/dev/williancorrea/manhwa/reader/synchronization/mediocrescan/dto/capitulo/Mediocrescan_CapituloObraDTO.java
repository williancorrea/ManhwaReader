package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_CapituloObraDTO {
  
  private Long id;
  private String nome;
  private Integer numero;
  private Object volume;
  private String imagem;
  @JsonProperty("lancado_em")
  private OffsetDateTime lancadoEm;
  @JsonProperty("criado_em")
  private OffsetDateTime criadoEm;
  private String descricao;
  @JsonProperty("tem_paginas")
  private Boolean temPaginas;
  private Integer totallinks;
}
