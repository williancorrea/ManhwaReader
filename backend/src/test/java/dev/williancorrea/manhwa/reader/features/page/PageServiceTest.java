package dev.williancorrea.manhwa.reader.features.page;

import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PageService")
class PageServiceTest {

  @Mock
  private PageRepository pageRepository;

  @InjectMocks
  private PageService pageService;

  private UUID pageId;
  private UUID chapterId;
  private Page testPage;
  private Chapter testChapter;

  @BeforeEach
  void setUp() {
    pageId = UUID.randomUUID();
    chapterId = UUID.randomUUID();
    testChapter = Chapter.builder().id(chapterId).build();
    testPage = Page.builder()
        .id(pageId)
        .chapter(testChapter)
        .pageNumber(1)
        .fileName("page-1.jpg")
        .type(PageType.IMAGE)
        .disabled(false)
        .build();
  }

  @Nested
  @DisplayName("countByChapterNumber()")
  class CountByChapterNumberTests {

    @Test
    @DisplayName("should count pages by chapter")
    void shouldCountPagesByChapter() {
      when(pageRepository.countByChapterNumber(chapterId)).thenReturn(5);

      var result = pageService.countByChapterNumber(testChapter);

      assertThat(result).isEqualTo(5);
      verify(pageRepository).countByChapterNumber(chapterId);
    }

    @Test
    @DisplayName("should return zero when chapter has no pages")
    void shouldReturnZeroWhenChapterHasNoPages() {
      when(pageRepository.countByChapterNumber(chapterId)).thenReturn(0);

      var result = pageService.countByChapterNumber(testChapter);

      assertThat(result).isZero();
      verify(pageRepository).countByChapterNumber(chapterId);
    }

    @Test
    @DisplayName("should count different chapter page counts correctly")
    void shouldCountDifferentChapterPageCountsCorrectly() {
      var chapter1Id = UUID.randomUUID();
      var chapter2Id = UUID.randomUUID();
      var chapter1 = Chapter.builder().id(chapter1Id).build();
      var chapter2 = Chapter.builder().id(chapter2Id).build();

      when(pageRepository.countByChapterNumber(chapter1Id)).thenReturn(10);
      when(pageRepository.countByChapterNumber(chapter2Id)).thenReturn(8);

      var result1 = pageService.countByChapterNumber(chapter1);
      var result2 = pageService.countByChapterNumber(chapter2);

      assertThat(result1).isEqualTo(10);
      assertThat(result2).isEqualTo(8);
    }
  }

  @Nested
  @DisplayName("findByNumberNotDisabled()")
  class FindByNumberNotDisabledTests {

    @Test
    @DisplayName("should find page by number and chapter when not disabled")
    void shouldFindPageByNumberAndChapterWhenNotDisabled() {
      when(pageRepository.findByNumberNotDisabled(chapterId, 1)).thenReturn(testPage);

      var result = pageService.findByNumberNotDisabled(testChapter, 1);

      assertThat(result)
          .isNotNull()
          .isEqualTo(testPage);
      verify(pageRepository).findByNumberNotDisabled(chapterId, 1);
    }

    @Test
    @DisplayName("should return null when page not found")
    void shouldReturnNullWhenPageNotFound() {
      when(pageRepository.findByNumberNotDisabled(chapterId, 99)).thenReturn(null);

      var result = pageService.findByNumberNotDisabled(testChapter, 99);

      assertThat(result).isNull();
      verify(pageRepository).findByNumberNotDisabled(chapterId, 99);
    }

    @Test
    @DisplayName("should not return disabled pages")
    void shouldNotReturnDisabledPages() {
      var disabledPage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(true)
          .build();

      when(pageRepository.findByNumberNotDisabled(chapterId, 1)).thenReturn(null);

      var result = pageService.findByNumberNotDisabled(testChapter, 1);

      assertThat(result).isNull();
      verify(pageRepository).findByNumberNotDisabled(chapterId, 1);
    }

    @Test
    @DisplayName("should find different pages by different numbers")
    void shouldFindDifferentPagesByDifferentNumbers() {
      var page1 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();

      when(pageRepository.findByNumberNotDisabled(chapterId, 1)).thenReturn(page1);
      when(pageRepository.findByNumberNotDisabled(chapterId, 2)).thenReturn(page2);

      var result1 = pageService.findByNumberNotDisabled(testChapter, 1);
      var result2 = pageService.findByNumberNotDisabled(testChapter, 2);

      assertThat(result1).isEqualTo(page1);
      assertThat(result2).isEqualTo(page2);
    }
  }

  @Nested
  @DisplayName("findAllByChapterIdNotDisabled()")
  class FindAllByChapterIdNotDisabledTests {

    @Test
    @DisplayName("should return all non-disabled pages for chapter ordered by page number")
    void shouldReturnAllNonDisabledPagesForChapterOrderedByPageNumber() {
      var page1 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var pages = List.of(page1, page2);

      when(pageRepository.findAllByChapterIdNotDisabled(chapterId)).thenReturn(pages);

      var result = pageService.findAllByChapterIdNotDisabled(chapterId);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(page1, page2);
      verify(pageRepository).findAllByChapterIdNotDisabled(chapterId);
    }

    @Test
    @DisplayName("should return empty list when no pages exist for chapter")
    void shouldReturnEmptyListWhenNoPagesExistForChapter() {
      when(pageRepository.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of());

      var result = pageService.findAllByChapterIdNotDisabled(chapterId);

      assertThat(result).isEmpty();
      verify(pageRepository).findAllByChapterIdNotDisabled(chapterId);
    }

    @Test
    @DisplayName("should return pages in ascending page number order")
    void shouldReturnPagesInAscendingPageNumberOrder() {
      var page1 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page5 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(5)
          .fileName("page-5.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page3 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(3)
          .fileName("page-3.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var pages = List.of(page1, page3, page5);

      when(pageRepository.findAllByChapterIdNotDisabled(chapterId)).thenReturn(pages);

      var result = pageService.findAllByChapterIdNotDisabled(chapterId);

      assertThat(result)
          .extracting(Page::getPageNumber)
          .containsExactly(1, 3, 5);
    }

    @Test
    @DisplayName("should exclude disabled pages")
    void shouldExcludeDisabledPages() {
      var enabledPage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();

      when(pageRepository.findAllByChapterIdNotDisabled(chapterId)).thenReturn(List.of(enabledPage));

      var result = pageService.findAllByChapterIdNotDisabled(chapterId);

      assertThat(result)
          .hasSize(1)
          .doesNotContainNull()
          .allMatch(page -> !page.getDisabled());
    }
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all pages")
    void shouldReturnAllPages() {
      var page1 = testPage;
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var pages = List.of(page1, page2);

      when(pageRepository.findAll()).thenReturn(pages);

      var result = pageService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(page1, page2);
      verify(pageRepository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no pages exist")
    void shouldReturnEmptyListWhenNoPagesExist() {
      when(pageRepository.findAll()).thenReturn(List.of());

      var result = pageService.findAll();

      assertThat(result).isEmpty();
      verify(pageRepository).findAll();
    }

    @Test
    @DisplayName("should return pages with different types")
    void shouldReturnPagesWithDifferentTypes() {
      var imagePage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var markdownPage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.md")
          .type(PageType.MARKDOWN)
          .disabled(false)
          .build();
      var pages = List.of(imagePage, markdownPage);

      when(pageRepository.findAll()).thenReturn(pages);

      var result = pageService.findAll();

      assertThat(result)
          .hasSize(2)
          .extracting(Page::getType)
          .contains(PageType.IMAGE, PageType.MARKDOWN);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should return page when found by id")
    void shouldReturnPageWhenFoundById() {
      when(pageRepository.findById(pageId)).thenReturn(Optional.of(testPage));

      var result = pageService.findById(pageId);

      assertThat(result)
          .isPresent()
          .contains(testPage);
      verify(pageRepository).findById(pageId);
    }

    @Test
    @DisplayName("should return empty optional when page not found")
    void shouldReturnEmptyOptionalWhenPageNotFound() {
      var nonExistentId = UUID.randomUUID();
      when(pageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

      var result = pageService.findById(nonExistentId);

      assertThat(result).isEmpty();
      verify(pageRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("should find correct page by id")
    void shouldFindCorrectPageById() {
      var page1 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();

      when(pageRepository.findById(page1.getId())).thenReturn(Optional.of(page1));
      when(pageRepository.findById(page2.getId())).thenReturn(Optional.of(page2));

      var result1 = pageService.findById(page1.getId());
      var result2 = pageService.findById(page2.getId());

      assertThat(result1).isPresent().contains(page1);
      assertThat(result2).isPresent().contains(page2);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save page and return saved entity")
    void shouldSavePageAndReturnSavedEntity() {
      when(pageRepository.save(testPage)).thenReturn(testPage);

      var result = pageService.save(testPage);

      assertThat(result)
          .isNotNull()
          .isEqualTo(testPage);
      verify(pageRepository).save(testPage);
    }

    @Test
    @DisplayName("should save page with all properties")
    void shouldSavePageWithAllProperties() {
      var page = Page.builder()
          .id(pageId)
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .content("image base64 content")
          .disabled(false)
          .build();

      when(pageRepository.save(page)).thenReturn(page);

      var result = pageService.save(page);

      assertThat(result)
          .satisfies(p -> {
            assertThat(p.getId()).isEqualTo(pageId);
            assertThat(p.getChapter()).isEqualTo(testChapter);
            assertThat(p.getPageNumber()).isEqualTo(1);
            assertThat(p.getFileName()).isEqualTo("page-1.jpg");
            assertThat(p.getType()).isEqualTo(PageType.IMAGE);
            assertThat(p.getContent()).isEqualTo("image base64 content");
            assertThat(p.getDisabled()).isFalse();
          });
      verify(pageRepository).save(page);
    }

    @Test
    @DisplayName("should save multiple pages")
    void shouldSaveMultiplePages() {
      var page1 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();
      var page2 = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(2)
          .fileName("page-2.jpg")
          .type(PageType.IMAGE)
          .disabled(false)
          .build();

      when(pageRepository.save(page1)).thenReturn(page1);
      when(pageRepository.save(page2)).thenReturn(page2);

      var result1 = pageService.save(page1);
      var result2 = pageService.save(page2);

      assertThat(result1).isEqualTo(page1);
      assertThat(result2).isEqualTo(page2);
    }

    @Test
    @DisplayName("should save markdown page with content")
    void shouldSaveMarkdownPageWithContent() {
      var markdownPage = Page.builder()
          .id(UUID.randomUUID())
          .chapter(testChapter)
          .pageNumber(1)
          .fileName("page-1.md")
          .type(PageType.MARKDOWN)
          .content("# Page content in markdown")
          .disabled(false)
          .build();

      when(pageRepository.save(markdownPage)).thenReturn(markdownPage);

      var result = pageService.save(markdownPage);

      assertThat(result)
          .satisfies(p -> {
            assertThat(p.getType()).isEqualTo(PageType.MARKDOWN);
            assertThat(p.getContent()).isEqualTo("# Page content in markdown");
          });
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when page exists")
    void shouldReturnTrueWhenPageExists() {
      when(pageRepository.existsById(pageId)).thenReturn(true);

      var result = pageService.existsById(pageId);

      assertThat(result).isTrue();
      verify(pageRepository).existsById(pageId);
    }

    @Test
    @DisplayName("should return false when page does not exist")
    void shouldReturnFalseWhenPageDoesNotExist() {
      var nonExistentId = UUID.randomUUID();
      when(pageRepository.existsById(nonExistentId)).thenReturn(false);

      var result = pageService.existsById(nonExistentId);

      assertThat(result).isFalse();
      verify(pageRepository).existsById(nonExistentId);
    }

    @Test
    @DisplayName("should check existence for multiple pages")
    void shouldCheckExistenceForMultiplePages() {
      var existingId = UUID.randomUUID();
      var nonExistingId = UUID.randomUUID();
      var anotherId = UUID.randomUUID();

      when(pageRepository.existsById(existingId)).thenReturn(true);
      when(pageRepository.existsById(nonExistingId)).thenReturn(false);
      when(pageRepository.existsById(anotherId)).thenReturn(true);

      assertThat(pageService.existsById(existingId)).isTrue();
      assertThat(pageService.existsById(nonExistingId)).isFalse();
      assertThat(pageService.existsById(anotherId)).isTrue();

      verify(pageRepository).existsById(existingId);
      verify(pageRepository).existsById(nonExistingId);
      verify(pageRepository).existsById(anotherId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete page by id")
    void shouldDeletePageById() {
      pageService.deleteById(pageId);

      verify(pageRepository).deleteById(pageId);
    }

    @Test
    @DisplayName("should delete multiple pages")
    void shouldDeleteMultiplePages() {
      var id1 = UUID.randomUUID();
      var id2 = UUID.randomUUID();
      var id3 = UUID.randomUUID();

      pageService.deleteById(id1);
      pageService.deleteById(id2);
      pageService.deleteById(id3);

      verify(pageRepository).deleteById(id1);
      verify(pageRepository).deleteById(id2);
      verify(pageRepository).deleteById(id3);
    }

    @Test
    @DisplayName("should delegate to repository delete method")
    void shouldDelegateToRepositoryDeleteMethod() {
      var idToDelete = UUID.randomUUID();

      pageService.deleteById(idToDelete);

      verify(pageRepository).deleteById(idToDelete);
    }
  }
}
