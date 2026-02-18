package dev.williancorrea.manhwa.reader.features.author;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorInput {
  @NotNull
  @Size(max = 255)
  private String name;
  @NotNull
  private AuthorType type;
}

