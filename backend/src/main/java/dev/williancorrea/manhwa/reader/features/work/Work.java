package dev.williancorrea.manhwa.reader.features.work;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.author.AuthorType;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.tag.TagGroupType;
import dev.williancorrea.manhwa.reader.features.volume.Volume;
import dev.williancorrea.manhwa.reader.features.work.cover.CoverType;
import dev.williancorrea.manhwa.reader.features.work.cover.WorkCover;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "work")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "synchronizations")
public class Work implements Serializable {

  @Serial
  private static final long serialVersionUID = -3222704934617470193L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private WorkType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private WorkStatus status;

  @Column(name = "release_year")
  private Integer releaseYear;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_rating")
  private WorkContentRating contentRating;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_language_id")
  private Language originalLanguage;

  private Boolean disabled;

  @Column(name = "slug", unique = true)
  private String slug;

  @Column(name = "chapter_numbers_reset_on_new_volume")
  private Boolean chapterNumbersResetOnNewVolume;

  @Enumerated(EnumType.STRING)
  @Column(name = "publication_demographic")
  private WorkPublicationDemographic publicationDemographic;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "relationship_id")
  private Work relationship;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkSynchronization> synchronizations;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkTitle> titles;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkSynopsis> synopses;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkLink> links;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkTag> tags;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkAuthor> authors;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Chapter> chapters;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Volume> volumes;

  @OneToMany(mappedBy = "work", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WorkCover> covers;

  @Formula("(SELECT MAX(CAST(c.number AS DECIMAL)) FROM chapter c WHERE c.work_id = id)")
  private Long chapterCount;

  public boolean hasSynchronizationOrigin(SynchronizationOriginType origin) {
    if (synchronizations == null) {
      return false;
    }
    return synchronizations.stream()
        .anyMatch(synchronization -> synchronization.getOrigin() == origin);
  }

  public boolean hasSiteType(SiteType origin) {
    if (links == null) {
      return false;
    }
    return links.stream()
        .anyMatch(link -> link.getCode() == origin);
  }

  public boolean hasTagWithNameOrAlias(TagGroupType group, String name) {
    if (tags == null) {
      return false;
    }
    return tags.stream()
        .anyMatch(tag -> tag.getTag().getGroup() == group
            && (
            (tag.getTag().getName() != null && tag.getTag().getName().equalsIgnoreCase(name))
                || (tag.getTag().getAlias1() != null && tag.getTag().getAlias1().equalsIgnoreCase(name))
                || (tag.getTag().getAlias2() != null && tag.getTag().getAlias2().equalsIgnoreCase(name))
                || (tag.getTag().getAlias3() != null && tag.getTag().getAlias3().equalsIgnoreCase(name))
        ));
  }

  public boolean hasAuthorOfType(AuthorType type, String name) {
    if (authors == null) {
      return false;
    }
    return authors.stream()
        .anyMatch(
            author -> author.getAuthor().getType() == type && author.getAuthor().getName().equalsIgnoreCase(name));
  }

  public boolean hasCoverWithOriginAndSize(SynchronizationOriginType origin, CoverType size) {
    if (covers == null) {
      return false;
    }
    return covers.stream()
        .anyMatch(cover -> cover.getOrigin() == origin && cover.getSize() == size);
  }

  public String getCoverUrl() {
    if (covers == null || covers.isEmpty()) {
      return null;
    }

    // First, try searching for the official cover.
    WorkCover officialCover = covers.stream()
        .filter(WorkCover::getIsOfficial)
        .findFirst()
        .orElse(null);

    // If you can't find an official one, pick the first one on the list.
    WorkCover cover = officialCover != null ? officialCover : covers.getFirst();

    // Returns the path to the cover.
    if (cover != null && cover.getFileName() != null) {
      String basePath = publicationDemographic != null
          ? publicationDemographic.name().toLowerCase()
          : WorkPublicationDemographic.UNKNOWN.name().toLowerCase();
      
      String workSlug = slug != null ? slug : WorkPublicationDemographic.UNKNOWN.name().toLowerCase();
      return String.format("/%s/%s/covers/%s", basePath, workSlug, cover.getFileName());
    }

    return null;
  }

  @PrePersist
  private void prePersist() {
    this.createdAt = OffsetDateTime.now();
  }

  @PreUpdate
  private void preUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
