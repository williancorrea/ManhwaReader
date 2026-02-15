package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkGenreOutput implements Serializable {
  private UUID workId;
  private UUID genreId;

  public WorkGenreOutput(WorkGenre workGenre) {
    this.workId = workGenre.getWork() != null ? workGenre.getWork().getId() : null;
    this.genreId = workGenre.getGenre() != null ? workGenre.getGenre().getId() : null;
  }
}
