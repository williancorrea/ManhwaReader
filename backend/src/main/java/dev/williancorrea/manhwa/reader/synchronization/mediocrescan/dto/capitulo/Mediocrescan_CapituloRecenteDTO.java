package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_CapituloRecenteDTO {
  
  private Long id;
  private Integer numero;
  private String nome;
  @JsonProperty("lancado_em")
  private Object lancadoEm;
  @JsonProperty("criado_em")
  private OffsetDateTime criadoEm;
}