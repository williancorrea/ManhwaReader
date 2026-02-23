package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;

import lombok.Data;

@Data
public class MangaDexRelationship {
  private String id;
  private String type;
  private MangaDexRelationshipAttributes attributes;
}