package dev.williancorrea.manhwa.reader.features.access.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
  @NotNull
  @Size(max = 50)
  private String username;
  @NotNull
  @Size(max = 255)
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
  private String email;
  @NotNull
  @Size(max = 255)
  private String passwordHash;
  private OffsetDateTime createdAt;
}

