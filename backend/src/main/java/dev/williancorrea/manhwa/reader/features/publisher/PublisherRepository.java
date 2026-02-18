package dev.williancorrea.manhwa.reader.features.publisher;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
}
