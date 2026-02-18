package dev.williancorrea.manhwa.reader.features.work;

import java.io.Serializable;
import dev.williancorrea.manhwa.reader.features.genre.Genre;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_genre")
@IdClass(WorkGenreId.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkGenre implements Serializable {
  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;
}
