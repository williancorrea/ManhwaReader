package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapituloObraDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_FormatoDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_StatusDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraDetalhadaResponse {
  
  private Long id;
  private String nome;
  @JsonProperty("titulos_alternativos")
  private List<Object> titulosAlternativos;
  private String descricao;
  private String imagem;
  private String banner;
  private String slug;
  private Mediocrescan_FormatoDTO formato;
  @JsonProperty("criada_em")
  private OffsetDateTime criadaEm;
  private List<Object> tags; // Array vazio no exemplo
  private Mediocrescan_StatusDTO status;
  @JsonProperty("total_capitulos")
  private Integer totalCapitulos;
  @JsonProperty("capitulos_importados")
  private Integer capitulosImportados;
  @JsonProperty("capitulos_nao_importados")
  private Integer capitulosNaoImportados;
  private List<Mediocrescan_CapituloObraDTO> capitulos;
  @JsonProperty("data_ultimo_cap")
  private OffsetDateTime dataUltimoCap;
  private Boolean home;
  private Boolean vip;
  private Boolean desativada;
  @JsonProperty("obr_novel_id")
  private Object obraNovelId;
  @JsonProperty("obra_novel")
  private Object obraNovel;
  @JsonProperty("obra_original")
  private Object obraOriginal;
}