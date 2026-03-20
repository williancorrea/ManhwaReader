package dev.williancorrea.manhwa.reader.features.chapter.notify;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChapterNotifyRepository extends JpaRepository<ChapterNotify, UUID> {

    @Query("SELECT cn FROM ChapterNotify cn " +
           "JOIN FETCH cn.work w " +
           "JOIN FETCH cn.chapter c " +
           "ORDER BY w.id, c.number desc")
    List<ChapterNotify> findAllWithWorkAndChapter();

    List<ChapterNotify> findByWorkId(UUID workId);

    void deleteByIdIn(List<UUID> ids);
}

