package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class MangaDexTag {
  private String id;
  private String type;

  @JsonProperty("attributes")
  private MangaDexTagAttributes attributes;

  @JsonProperty("relationships")
  private List<Object> relationships;
}