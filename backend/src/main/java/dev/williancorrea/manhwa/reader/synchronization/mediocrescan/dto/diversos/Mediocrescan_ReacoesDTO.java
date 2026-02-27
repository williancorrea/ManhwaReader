package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ReacoesDTO {
  
  @JsonProperty("muito_bom")
  private Integer muitoBom;
  private Integer bom;
  private Integer medio;
  private Integer ruim;
  @JsonProperty("muito_ruim")
  private Integer muitoRuim;
  private Integer total;
  @JsonProperty("minha_reacao")
  private Object minhaReacao;
}