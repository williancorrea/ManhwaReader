package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_FormatoDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_StatusDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_TagDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraDTO {
  
  private Long id;
  private String nome;
  private String descricao;
  private String imagem;
  private String banner;
  private String slug;
  private Mediocrescan_FormatoDTO formato;
  @JsonProperty("criada_em")
  private OffsetDateTime criadaEm;
  private List<Mediocrescan_TagDTO> tags;
  private Mediocrescan_StatusDTO status;
  @JsonProperty("total_capitulos")
  private Integer totalCapitulos;
  @JsonProperty("data_ultimo_cap")
  private OffsetDateTime dataUltimoCap;
  private Boolean home;
  private Boolean vip;
  private Boolean desativada;
  @JsonProperty("obra_novel")
  private Object obraNovel;
}