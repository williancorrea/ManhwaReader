package dev.williancorrea.manhwa.reader.features;

import dev.williancorrea.manhwa.reader.features.Language;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, UUID> {
}
