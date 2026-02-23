package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;


import java.util.List;
import lombok.Data;

@Data
public class MangaDexData {
  private String id;
  private String type;
  private MangaDexAttributes attributes;
  private List<MangaDexRelationship> relationships;
}