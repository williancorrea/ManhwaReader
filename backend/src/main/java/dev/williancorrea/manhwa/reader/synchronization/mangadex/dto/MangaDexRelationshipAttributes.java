package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class MangaDexRelationshipAttributes {
  private String name;

  @JsonProperty("imageUrl")
  private String imageUrl;

  private Map<String, String> biography;
  private String twitter;
  private String pixiv;
  private String melonBook;
  private String fanBox;
  private String booth;
  private String namicomi;
  private String nicoVideo;
  private String skeb;
  private String fantia;
  private String tumblr;
  private String youtube;
  private String weibo;
  private String naver;
  private String website;
  private Integer version;

  // to cover_art
  private String description;
  private String volume;
  private String fileName;
  private String locale;
}