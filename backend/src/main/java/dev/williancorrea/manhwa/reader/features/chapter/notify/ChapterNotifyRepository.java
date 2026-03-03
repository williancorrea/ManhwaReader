package dev.williancorrea.manhwa.reader.features.chapter.notify;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterNotifyRepository extends JpaRepository<ChapterNotify, UUID> {

}

