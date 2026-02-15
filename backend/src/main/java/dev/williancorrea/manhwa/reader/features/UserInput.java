package dev.williancorrea.manhwa.reader.features;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
  @Email
  private String email;
  @NotNull
  @Size(max = 255)
  private String passwordHash;
  @NotNull
  @Size(max = 20)
  private String role;
  private OffsetDateTime createdAt;
}

