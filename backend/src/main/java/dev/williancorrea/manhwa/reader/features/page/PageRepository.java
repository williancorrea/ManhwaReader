package dev.williancorrea.manhwa.reader.features.page;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query("""
      SELECT p FROM Page p WHERE p.chapter.id = :chapterId AND p.disabled = false ORDER BY p.pageNumber ASC
      """)
  List<Page> findAllByChapterIdNotDisabled(@Param("chapterId") UUID chapterId);
}

