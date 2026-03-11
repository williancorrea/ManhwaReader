package dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto;

import lombok.Data;

@Data
public class Mango_PaginationDTO {

  public boolean hasNextPage;
  public boolean hasPreviousPage;
  public Integer limite;
  public Integer pagina;
  public Integer total;
  public Integer totalPaginas;
}