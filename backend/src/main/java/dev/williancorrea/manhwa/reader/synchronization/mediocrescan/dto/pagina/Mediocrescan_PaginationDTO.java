package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.pagina;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_PaginationDTO {
  
  @JsonProperty("currentPage")
  private Integer currentPage;
  @JsonProperty("totalPages")
  private Integer totalPages;
  @JsonProperty("totalItems")
  private Integer totalItems;
  @JsonProperty("itemsPerPage")
  private Integer itemsPerPage;
  @JsonProperty("hasNextPage")
  private Boolean hasNextPage;
  @JsonProperty("hasPreviousPage")
  private Boolean hasPreviousPage;
}