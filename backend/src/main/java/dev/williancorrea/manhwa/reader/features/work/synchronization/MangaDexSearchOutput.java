package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexData;
import dev.williancorrea.manhwa.reader.scraper.mangadex.dto.MangaDexRelationship;

public record MangaDexSearchOutput(
    String id,
    String coverUrl,
    List<String> titles
) {

  private static final String MANGADEX_COVERS_BASE = "https://uploads.mangadex.org/covers";

  public static MangaDexSearchOutput fromMangaDexData(MangaDexData data) {
    List<String> allTitles = new ArrayList<>();

    if (data.getAttributes() != null) {
      if (data.getAttributes().getTitle() != null) {
        allTitles.addAll(data.getAttributes().getTitle().values());
      }
      if (data.getAttributes().getAltTitles() != null) {
        for (Map<String, String> altTitle : data.getAttributes().getAltTitles()) {
          allTitles.addAll(altTitle.values());
        }
      }
    }

    String coverUrl = null;
    if (data.getRelationships() != null) {
      coverUrl = data.getRelationships().stream()
          .filter(r -> "cover_art".equals(r.getType()))
          .findFirst()
          .map(MangaDexRelationship::getAttributes)
          .map(attrs -> attrs.getFileName())
          .map(fileName -> String.format("%s/%s/%s.256.jpg", MANGADEX_COVERS_BASE, data.getId(), fileName))
          .orElse(null);
    }

    return new MangaDexSearchOutput(data.getId(), coverUrl, allTitles);
  }
}
