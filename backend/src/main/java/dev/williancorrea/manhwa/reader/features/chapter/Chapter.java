package dev.williancorrea.manhwa.reader.features.chapter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.language.Language;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import dev.williancorrea.manhwa.reader.features.volume.Volume;
import dev.williancorrea.manhwa.reader.features.work.Work;
import dev.williancorrea.manhwa.reader.utils.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chapter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chapter implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  @NotNull
  @NotEmpty
  @Size(min = 1, max = 10)
  @Column(nullable = false)
  private String number;

  @NotNull
  @NotEmpty
  @Size(min = 4, max = 4)
  @Column(name = "number_formatted", nullable = false)
  private String numberFormatted;

  @NotNull
  @NotEmpty
  @Size(min = 4, max = 4)
  @Column(name = "number_version", nullable = false)
  private String numberVersion;

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
  @JoinColumn(name = "scanlator_id", nullable = false)
  private Scanlator scanlator;

  /**
   * Used to indicate whether the chapter was successfully synchronized.
   */
  private Boolean synced;


  private Boolean disabled;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "published_at")
  private OffsetDateTime publishedAt;

  public void setNumberFormatted(String numberFormatted) {
    this.numberFormatted = StringUtils.completeWithZeroZeroToLeft(numberFormatted, 4);
  }

  public String getNumberFormatted() {
    return StringUtils.completeWithZeroZeroToLeft(numberFormatted, 4);
  }

  /**
   * Returns the chapter number with the version number (123, 124.1) if it is not 0000.
   * @return
   */
  public String getNumberWithVersionInteger() {
    return Integer.parseInt(numberFormatted) +
        (!Objects.equals(numberVersion, "0000")
            ? "." + Integer.parseInt(numberVersion)
            : "");

  }
}
