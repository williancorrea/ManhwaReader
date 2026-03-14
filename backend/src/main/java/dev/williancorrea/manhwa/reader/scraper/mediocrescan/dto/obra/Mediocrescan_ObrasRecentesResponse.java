package dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.obra;

import java.util.List;
import dev.williancorrea.manhwa.reader.scraper.mediocrescan.dto.pagina.Mediocrescan_PaginationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObrasRecentesResponse {
  
  private List<Mediocrescan_ObraRecenteDTO> data;
  private Mediocrescan_PaginationDTO pagination;
}