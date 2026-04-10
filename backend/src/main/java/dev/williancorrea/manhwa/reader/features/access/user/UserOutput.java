package dev.williancorrea.manhwa.reader.features.access.user;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
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
    private String name;
    private String email;
    private String avatarUrl;
    private OffsetDateTime createdAt;
    private List<String> roles;

    public UserOutput(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
        this.createdAt = user.getCreatedAt();
    }

    public UserOutput(User user, List<String> roles) {
        this(user);
        this.roles = roles;
    }
}
