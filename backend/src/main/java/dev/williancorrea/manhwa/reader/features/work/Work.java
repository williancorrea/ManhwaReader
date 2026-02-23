package dev.williancorrea.manhwa.reader.features.work;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.publisher.Publisher;
import dev.williancorrea.manhwa.reader.features.storage.FileStorage;
import dev.williancorrea.manhwa.reader.features.work.link.SiteType;
import dev.williancorrea.manhwa.reader.features.work.link.WorkLink;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import dev.williancorrea.manhwa.reader.features.work.synchronization.WorkSynchronization;
import dev.williancorrea.manhwa.reader.features.work.synopsis.WorkSynopsis;
import dev.williancorrea.manhwa.reader.features.work.tag.WorkTag;
import dev.williancorrea.manhwa.reader.features.work.title.WorkTitle;
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
  @JoinColumn(name = "cover_image_id")
  private FileStorage coverImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher_id")
  private Publisher publisher;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_language_id")
  private Language originalLanguage;

  private Boolean disabled;

  @Enumerated(EnumType.STRING)
  @Column(name = "publication_demographic")
  private WorkPublicationDemographic publicationDemographic;

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

  public boolean getSynchronizationsContains(SynchronizationOriginType origin) {
    return synchronizations.stream()
        .anyMatch(synchronization -> synchronization.getOrigin() == origin);
  }

  public boolean getLinksContains(SiteType origin) {
    return links.stream()
        .anyMatch(link -> link.getCode() == origin);
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
