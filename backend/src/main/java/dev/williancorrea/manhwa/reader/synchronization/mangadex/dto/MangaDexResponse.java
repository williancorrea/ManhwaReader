package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;


import java.util.List;
import lombok.Data;

@Data
public class MangaDexResponse {
  private String result;
  private String response;
  private List<MangaDexData> data;
  private Integer limit;
  private Integer offset;
  private Integer total;
}