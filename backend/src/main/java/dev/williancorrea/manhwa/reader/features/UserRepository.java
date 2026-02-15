package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
}
