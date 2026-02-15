package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutput implements Serializable {
  private UUID id;
  private String username;
  private String email;
  private String role;
  private OffsetDateTime createdAt;

  public UserOutput(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.role = user.getRole();
    this.createdAt = user.getCreatedAt();
  }
}
