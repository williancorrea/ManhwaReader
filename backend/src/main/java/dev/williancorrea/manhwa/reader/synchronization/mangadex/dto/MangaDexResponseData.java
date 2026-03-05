package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;

import lombok.Data;

@Data
public class MangaDexResponseData {
  private String result;
  private String response;
  private MangaDexData data;
}