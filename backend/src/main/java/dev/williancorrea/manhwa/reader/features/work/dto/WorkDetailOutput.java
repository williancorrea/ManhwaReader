package dev.williancorrea.manhwa.reader.features.work.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.library.Library;
import dev.williancorrea.manhwa.reader.features.rating.Rating;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.features.work.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.WorkTitle;

public record WorkDetailOutput(
    UUID id,
    String slug,
    String title,
    List<AlternativeTitleOutput> alternativeTitles,
    String synopsis,
    String coverUrl,
    String type,
    String status,
    Integer releaseYear,
    String contentRating,
    String publicationDemographic,
    String originalLanguage,
    String originalLanguageFlag,
    String originalLanguageName,
    List<TagOutput> tags,
    List<AuthorOutput> authors,
    List<LinkOutput> links,
    Long chapterCount,
    String userLibraryStatus,
    Integer userRating
) {

  public record AlternativeTitleOutput(String title, String language, String languageFlag, String languageName, Boolean isOfficial) {
  }

  public record TagOutput(String name, String group) {
  }

  public record AuthorOutput(String name, String type) {
  }

  public record LinkOutput(String siteCode, String url) {
  }

  public static WorkDetailOutput fromEntity(Work work, String storage,
                                            Optional<Library> library,
                                            Optional<Rating> rating) {
    String title = null;
    List<AlternativeTitleOutput> alternativeTitles = List.of();

    if (work.getTitles() != null && !work.getTitles().isEmpty()) {
      title = work.getTitles().stream()
          .filter(t -> Boolean.TRUE.equals(t.getIsOfficial()))
          .map(WorkTitle::getTitle)
          .findFirst()
          .orElse(work.getTitles().getFirst().getTitle());

      alternativeTitles = work.getTitles().stream()
          .map(t -> new AlternativeTitleOutput(
              t.getTitle(),
              t.getLanguage() != null ? t.getLanguage().getCode() : null,
              t.getLanguage() != null ? t.getLanguage().getFlag() : null,
              t.getLanguage() != null ? t.getLanguage().getName() : null,
              t.getIsOfficial()))
          .sorted(Comparator.comparing(
              AlternativeTitleOutput::language,
              Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
          .toList();
    }

    String synopsis = null;
    if (work.getSynopses() != null && !work.getSynopses().isEmpty()) {
      synopsis = work.getSynopses().stream()
          .filter(s -> "pt-br".equals(s.getLanguage().getCode()))
          .map(WorkSynopsis::getDescription)
          .findFirst()
          .orElseGet(() -> work.getSynopses().stream()
              .filter(s -> "en".equals(s.getLanguage().getCode()))
              .map(WorkSynopsis::getDescription)
              .findFirst()
              .orElse(work.getSynopses().getFirst().getDescription()));
    }

    String coverUrl = work.getCoverUrl() != null ? storage + "/" + work.getCoverUrl() : null;

    List<TagOutput> tags = work.getTags() != null
        ? work.getTags().stream()
          .map(wt -> new TagOutput(wt.getTag().getAlias1(), wt.getTag().getGroup().name()))
          .toList()
        : List.of();

    List<AuthorOutput> authors = work.getAuthors() != null
        ? work.getAuthors().stream()
          .map(wa -> new AuthorOutput(wa.getAuthor().getName(), wa.getAuthor().getType().name()))
          .toList()
        : List.of();

    List<LinkOutput> links = work.getLinks() != null
        ? work.getLinks().stream()
          .map(wl -> new LinkOutput(wl.getCode().name(), wl.getSite().getUrl() + "/" + wl.getLink()))
          .toList()
        : List.of();

    return new WorkDetailOutput(
        work.getId(),
        work.getSlug(),
        title,
        alternativeTitles,
        synopsis,
        coverUrl,
        work.getType() != null ? work.getType().name() : null,
        work.getStatus() != null ? work.getStatus().name() : null,
        work.getReleaseYear(),
        work.getContentRating() != null ? work.getContentRating().name() : null,
        work.getPublicationDemographic() != null ? work.getPublicationDemographic().name() : null,
        work.getOriginalLanguage() != null ? work.getOriginalLanguage().getCode() : null,
        work.getOriginalLanguage() != null ? work.getOriginalLanguage().getFlag() : null,
        work.getOriginalLanguage() != null ? work.getOriginalLanguage().getName() : null,
        tags,
        authors,
        links,
        work.getChapterCount(),
        library.map(l -> l.getStatus().name()).orElse(null),
        rating.map(Rating::getScore).orElse(null)
    );
  }
}
