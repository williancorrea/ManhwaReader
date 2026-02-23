package dev.williancorrea.manhwa.reader.synchronization.mangadex.dto;


import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MangaDexAttributes {
  private Map<String, String> title;
  private List<Map<String, String>> altTitles;
  private Map<String, String> description;
  private Boolean isLocked;
  private MangaDexLinks links;
  private List<Map<String, String>> officialLinks;
  private String originalLanguage;
  private String lastVolume;
  private String lastChapter;
  private String publicationDemographic;
  private String status;
  private Integer year;
  private String contentRating;
  private List<MangaDexTag> tags;
  private String state;
  private Boolean chapterNumbersResetOnNewVolume;
  private Integer version;
  private List<String> availableTranslatedLanguages;
  private String latestUploadedChapter;
}