package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MangaDexLinks {
  @JsonProperty("al")
  private String anilist;

  @JsonProperty("ap")
  private String animePlanet;

  @JsonProperty("bw")
  private String bookWalker;

  @JsonProperty("kt")
  private String kitsu;

  @JsonProperty("mu")
  private String mangaUpdates;

  @JsonProperty("mal")
  private String myAnimeList;

  @JsonProperty("nu")
  private String novelUpdates;
  
  @JsonProperty("raw")
  private String raw;
}