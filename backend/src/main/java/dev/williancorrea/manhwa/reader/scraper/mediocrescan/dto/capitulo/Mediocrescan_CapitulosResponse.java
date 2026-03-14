package dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.capitulo;

import java.util.List;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.pagina.Mediocrescan_PaginationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_CapitulosResponse {
  
  private List<Mediocrescan_CapituloDTO> data;
  private Mediocrescan_PaginationDTO pagination;
}
