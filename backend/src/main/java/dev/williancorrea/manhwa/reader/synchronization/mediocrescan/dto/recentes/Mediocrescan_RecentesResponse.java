package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.recentes;

import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_PaginacaoRecenteDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraRecenteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_RecentesResponse {
  
  private List<Mediocrescan_ObraRecenteDTO> data;
  private Mediocrescan_PaginacaoRecenteDTO pagination;
}