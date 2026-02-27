package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.capitulo.Mediocrescan_CapituloSimplificadoDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_FormatoDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_StatusDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraCompletaDTO {
  
  private Long id;
  private String nome;
  private String imagem;
  private Boolean vip;
  private Boolean desativada;
  private Mediocrescan_FormatoDTO formato;
  private Mediocrescan_StatusDTO status;
  private List<Mediocrescan_CapituloSimplificadoDTO> capitulos;
}