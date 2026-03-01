package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_FormatoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraOriginalDTO {

  private Long id;
  private String nome;
  private String slug;
  private String imagem;
  private Mediocrescan_FormatoDTO formato;
}