package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_ObraNovelDTO {

  private Long id;
  private String nome;
  private String slug;
}