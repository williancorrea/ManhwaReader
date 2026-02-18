package dev.williancorrea.manhwa.reader.features.access.user;

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
public class UserAccessGroupOutput implements Serializable {
  private UUID userId;
  private UUID accessGroupId;

  public UserAccessGroupOutput(UserAccessGroup userAccessGroup) {
    this.userId = userAccessGroup.getUser() != null ? userAccessGroup.getUser().getId() : null;
    this.accessGroupId = userAccessGroup.getAccessGroup() != null ? userAccessGroup.getAccessGroup().getId() : null;
  }
}
