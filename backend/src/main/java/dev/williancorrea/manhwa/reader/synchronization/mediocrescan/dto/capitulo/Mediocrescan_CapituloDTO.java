package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraSimplificadaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_CapituloDTO {
  
  private Long id;
  private String nome;
  private String descricao;
  private Integer numero;
  private String volume;
  @JsonProperty("lancado_em")
  private OffsetDateTime lancadoEm;
  @JsonProperty("criado_em")
  private OffsetDateTime criadoEm;
  private String tipo;
  @JsonProperty("tem_paginas")
  private Boolean temPaginas;
  private List<Object> paginas2; // Array vazio no exemplo
  private List<Object> links; // Array vazio no exemplo
  private Mediocrescan_ObraSimplificadaDTO obra;
}