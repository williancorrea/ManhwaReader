package dev.williancorrea.manhwa.reader.features.rating;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

  Optional<Rating> findByUser_IdAndWork_Id(UUID userId, UUID workId);
}

