package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Rating;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
