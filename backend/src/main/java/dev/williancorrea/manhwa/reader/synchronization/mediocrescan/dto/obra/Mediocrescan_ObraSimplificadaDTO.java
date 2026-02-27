package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraSimplificadaDTO {
  
  @JsonProperty("obr_id")
  private Long obraId;
  @JsonProperty("obr_nome")
  private String obraNome;
}