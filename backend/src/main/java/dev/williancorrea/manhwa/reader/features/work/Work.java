package dev.williancorrea.manhwa.reader.features.work;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.publisher.Publisher;
import dev.williancorrea.manhwa.reader.features.storage.FileStorage;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Work implements Serializable {

  @Serial
  private static final long serialVersionUID = -7934835457488837782L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(name = "original_title", nullable = false)
  private String originalTitle;

  private String synopsis;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private WorkType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private WorkStatus status;

  @Column(name = "release_year")
  private Integer releaseYear;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cover_image_id")
  private FileStorage coverImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher_id")
  private Publisher publisher;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
