package dev.williancorrea.manhwa.reader.features.rating;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
  List<Rating> findAllByWork_Id(UUID workId);
  List<Rating> findAllByUser_Id(UUID userId);
}

