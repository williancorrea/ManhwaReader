package dev.williancorrea.manhwa.reader.scraper.mangotoons.dto;

import java.util.List;
import lombok.Data;

@Data
public class Mango_ObraCollectionDTO {

  public boolean sucesso;
  public List<Mango_ObraDTO> obras;
  public Mango_PaginationDTO pagination;
}