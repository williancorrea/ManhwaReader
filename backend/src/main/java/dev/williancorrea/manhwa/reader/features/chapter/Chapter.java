package dev.williancorrea.manhwa.reader.features.chapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.volume.Volume;
import dev.williancorrea.manhwa.reader.features.work.Work;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chapter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal number;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

  @Column(name = "release_date")
  private LocalDate releaseDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "volume_id")
  private Volume volume;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scanlator_id")
  private Scanlator scanlator;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;
}
