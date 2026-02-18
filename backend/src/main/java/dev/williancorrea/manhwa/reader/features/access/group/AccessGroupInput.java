package dev.williancorrea.manhwa.reader.features.access.group;

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
public class AccessGroupInput {
  @NotNull
  @Size(max = 100)
  private String name;
}
