package dev.williancorrea.manhwa.reader.features.scheduling;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulingRepository extends JpaRepository<Scheduling, UUID> {
  List<Scheduling> findByActiveTrue();
}
