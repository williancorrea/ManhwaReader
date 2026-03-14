package dev.williancorrea.manhwa.reader.scraper.mangotoons.dto;

import java.util.List;
import lombok.Data;

@Data
public class Mango_TagColletcionDTO {

  public boolean sucesso;

  public List<Mango_TagDTO> tags;

}