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
public class GenreOutput implements Serializable {
  private UUID id;
  private String name;

  public GenreOutput(Genre genre) {
    this.id = genre.getId();
    this.name = genre.getName();
  }
}
