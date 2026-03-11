package dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto;

import java.util.List;
import lombok.Data;

@Data
public class Mango_FormatoColletcionDTO {

  public boolean sucesso;

  public List<Mango_FormatoDTO> formatos;

}