package dev.williancorrea.manhwa.reader.features;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  @Column(name = "original_title")
  private String originalTitle;

  private String synopsis;

  @Enumerated(EnumType.STRING)
  private WorkType type;

  @Enumerated(EnumType.STRING)
  private WorkStatus status;

  @Column(name = "release_year")
  private Integer releaseYear;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
