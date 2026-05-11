package dev.williancorrea.manhwa.reader.features.access.user;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.email = ?1")
    Optional<UUID> findIdByEmail(String email);
}
