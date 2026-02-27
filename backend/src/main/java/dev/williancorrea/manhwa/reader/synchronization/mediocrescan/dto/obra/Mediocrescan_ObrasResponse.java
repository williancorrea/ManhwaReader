package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.pagina.Mediocrescan_PaginationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObrasResponse {
  
  private List<Mediocrescan_ObraDTO> data;
  private Mediocrescan_PaginationDTO pagination;
}