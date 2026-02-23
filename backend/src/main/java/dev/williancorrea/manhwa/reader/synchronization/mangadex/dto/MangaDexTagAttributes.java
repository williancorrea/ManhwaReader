package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;

import java.util.Map;
import lombok.Data;

@Data
public class MangaDexTagAttributes {
  private Map<String, String> name;
  private Map<String, String> description;
  private String group;
  private Integer version;
}