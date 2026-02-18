package dev.williancorrea.manhwa.reader.features.access.user;

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
  private OffsetDateTime createdAt;

  public UserOutput(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.createdAt = user.getCreatedAt();
  }
}
