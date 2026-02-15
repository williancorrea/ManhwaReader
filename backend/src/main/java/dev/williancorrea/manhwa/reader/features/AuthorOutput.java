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
public class AuthorOutput implements Serializable {
  private UUID id;
  private String name;
  private AuthorType type;

  public AuthorOutput(Author author) {
    this.id = author.getId();
    this.name = author.getName();
    this.type = author.getType();
  }
}
