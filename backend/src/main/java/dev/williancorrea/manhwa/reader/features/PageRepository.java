package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Page;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, UUID> {
}
