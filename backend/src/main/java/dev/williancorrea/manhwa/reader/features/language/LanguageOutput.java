package dev.williancorrea.manhwa.reader.features.language;

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
public class LanguageOutput implements Serializable {
  private UUID id;
  private String code;
  private String name;

  public LanguageOutput(Language language) {
    this.id = language.getId();
    this.code = language.getCode();
    this.name = language.getName();
  }
}
