package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapituloRecenteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraRecenteDTO {
  
  private Long id;
  private String nome;
  private String imagem;
  private String slug;
  @JsonProperty("atualizado_em")
  private OffsetDateTime atualizadoEm;
  @JsonProperty("formt_id")
  private Integer formatoId;
  @JsonProperty("capitulo_numero")
  private Integer capituloNumero;
  @JsonProperty("lancado_em")
  private Object lancadoEm;
  private List<Mediocrescan_CapituloRecenteDTO> capitulos;
}