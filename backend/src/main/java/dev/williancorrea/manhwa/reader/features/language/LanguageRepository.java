package dev.williancorrea.manhwa.reader.features.language;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, UUID> {

  Optional<Language> findByCode(String code);
}
