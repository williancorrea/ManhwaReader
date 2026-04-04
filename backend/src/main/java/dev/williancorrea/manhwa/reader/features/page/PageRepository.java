package dev.williancorrea.manhwa.reader.features.page;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PageRepository extends JpaRepository<Page, UUID> {

  @Query(
      value = """
            select count(p) from Page p where p.chapter.id = :chapterUUID
          """)
  int countByChapterNumber(UUID chapterUUID);

  @Query(
      value = """
          select p from Page p where p.chapter.id = :chapterID
              and p.pageNumber = :pageNumber
              and p.disabled = false
          """
  )
  Page findByNumberNotDisabled(UUID chapterID, Integer pageNumber);
}

