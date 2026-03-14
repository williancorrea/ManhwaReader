package dev.williancorrea.manhwa.reader.scraper.mangadex.dto;

import lombok.Data;

@Data
public class MangaDexRelationship {
  private String id;
  private String type;
  private MangaDexRelationshipAttributes attributes;
}