package dev.williancorrea.manhwa.reader.features.progress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadingProgressService")
class ReadingProgressServiceTest {

  @Mock
  private ReadingProgressRepository readingProgressRepository;

  @InjectMocks
  private ReadingProgressService readingProgressService;

  private UUID progressId;
  private UUID userId;
  private UUID chapterId;
  private UUID workId;
  private User user;
  private Chapter chapter;
  private ReadingProgress readingProgress;

  @BeforeEach
  void setUp() {
    progressId = UUID.randomUUID();
    userId = UUID.randomUUID();
    chapterId = UUID.randomUUID();
    workId = UUID.randomUUID();

    user = User.builder().id(userId).build();
    chapter = Chapter.builder().id(chapterId).build();
    readingProgress = ReadingProgress.builder()
        .id(progressId)
        .user(user)
        .chapter(chapter)
        .pageNumber(5)
        .lastReadAt(OffsetDateTime.now())
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("deve retornar todos os registros de progresso de leitura")
    void shouldReturnAllReadingProgress() {
      var readingProgress2 = ReadingProgress.builder()
          .id(UUID.randomUUID())
          .user(user)
          .chapter(chapter)
          .build();
      var progressList = List.of(readingProgress, readingProgress2);

      when(readingProgressRepository.findAll()).thenReturn(progressList);

      var result = readingProgressService.findAll();

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .contains(readingProgress, readingProgress2);
      verify(readingProgressRepository).findAll();
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há progressos")
    void shouldReturnEmptyListWhenNoProgressFound() {
      when(readingProgressRepository.findAll()).thenReturn(List.of());

      var result = readingProgressService.findAll();

      assertThat(result).isEmpty();
      verify(readingProgressRepository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("deve retornar progresso quando encontrado")
    void shouldReturnReadingProgressWhenFound() {
      when(readingProgressRepository.findById(progressId))
          .thenReturn(Optional.of(readingProgress));

      var result = readingProgressService.findById(progressId);

      assertThat(result)
          .isPresent()
          .contains(readingProgress);
      verify(readingProgressRepository).findById(progressId);
    }

    @Test
    @DisplayName("deve retornar Optional vazio quando progresso não encontrado")
    void shouldReturnEmptyOptionalWhenNotFound() {
      when(readingProgressRepository.findById(progressId))
          .thenReturn(Optional.empty());

      var result = readingProgressService.findById(progressId);

      assertThat(result).isEmpty();
      verify(readingProgressRepository).findById(progressId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("deve salvar e retornar progresso de leitura")
    void shouldSaveAndReturnReadingProgress() {
      when(readingProgressRepository.save(readingProgress))
          .thenReturn(readingProgress);

      var result = readingProgressService.save(readingProgress);

      assertThat(result)
          .isNotNull()
          .isEqualTo(readingProgress);
      verify(readingProgressRepository).save(readingProgress);
    }

    @Test
    @DisplayName("deve salvar novo progresso de leitura")
    void shouldSaveNewReadingProgress() {
      var newProgress = ReadingProgress.builder()
          .user(user)
          .chapter(chapter)
          .pageNumber(1)
          .build();

      when(readingProgressRepository.save(newProgress))
          .thenReturn(newProgress);

      var result = readingProgressService.save(newProgress);

      assertThat(result).isNotNull().isEqualTo(newProgress);
      verify(readingProgressRepository).save(newProgress);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("deve retornar verdadeiro quando progresso existe")
    void shouldReturnTrueWhenProgressExists() {
      when(readingProgressRepository.existsById(progressId))
          .thenReturn(true);

      var result = readingProgressService.existsById(progressId);

      assertThat(result).isTrue();
      verify(readingProgressRepository).existsById(progressId);
    }

    @Test
    @DisplayName("deve retornar falso quando progresso não existe")
    void shouldReturnFalseWhenProgressDoesNotExist() {
      when(readingProgressRepository.existsById(progressId))
          .thenReturn(false);

      var result = readingProgressService.existsById(progressId);

      assertThat(result).isFalse();
      verify(readingProgressRepository).existsById(progressId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("deve deletar progresso por id")
    void shouldDeleteReadingProgressById() {
      readingProgressService.deleteById(progressId);

      verify(readingProgressRepository).deleteById(progressId);
    }

    @Test
    @DisplayName("deve lidar com deleção de progresso inexistente")
    void shouldHandleDeletionOfNonExistentProgress() {
      var nonExistentId = UUID.randomUUID();

      readingProgressService.deleteById(nonExistentId);

      verify(readingProgressRepository).deleteById(nonExistentId);
    }
  }

  @Nested
  @DisplayName("findByUserAndChapter()")
  class FindByUserAndChapterTests {

    @Test
    @DisplayName("deve retornar progresso quando usuário e capítulo existem")
    void shouldReturnProgressWhenUserAndChapterFound() {
      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapterId))
          .thenReturn(Optional.of(readingProgress));

      var result = readingProgressService.findByUserAndChapter(user, chapter);

      assertThat(result)
          .isPresent()
          .contains(readingProgress);
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapterId);
    }

    @Test
    @DisplayName("deve retornar Optional vazio quando não há progresso para usuário e capítulo")
    void shouldReturnEmptyOptionalWhenProgressNotFound() {
      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapterId))
          .thenReturn(Optional.empty());

      var result = readingProgressService.findByUserAndChapter(user, chapter);

      assertThat(result).isEmpty();
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapterId);
    }
  }

  @Nested
  @DisplayName("saveOrUpdate()")
  class SaveOrUpdateTests {

    @Test
    @DisplayName("deve atualizar progresso existente")
    void shouldUpdateExistingProgress() {
      var existingProgress = ReadingProgress.builder()
          .id(progressId)
          .user(user)
          .chapter(chapter)
          .pageNumber(3)
          .lastReadAt(OffsetDateTime.now().minusHours(1))
          .build();

      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapterId))
          .thenReturn(Optional.of(existingProgress));
      when(readingProgressRepository.save(any(ReadingProgress.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = readingProgressService.saveOrUpdate(user, chapter);

      assertThat(result).isNotNull();
      assertThat(result.getLastReadAt()).isNotNull();
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapterId);
      verify(readingProgressRepository).save(any(ReadingProgress.class));
    }

    @Test
    @DisplayName("deve criar novo progresso quando não existir")
    void shouldCreateNewProgressWhenNotExists() {
      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapterId))
          .thenReturn(Optional.empty());
      when(readingProgressRepository.save(any(ReadingProgress.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      var result = readingProgressService.saveOrUpdate(user, chapter);

      assertThat(result).isNotNull();
      assertThat(result.getUser()).isEqualTo(user);
      assertThat(result.getChapter()).isEqualTo(chapter);
      assertThat(result.getLastReadAt()).isNotNull();
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapterId);
      verify(readingProgressRepository).save(any(ReadingProgress.class));
    }
  }

  @Nested
  @DisplayName("deleteByUserAndChapter()")
  class DeleteByUserAndChapterTests {

    @Test
    @DisplayName("deve deletar progresso de usuário e capítulo específicos")
    void shouldDeleteProgressByUserAndChapter() {
      readingProgressService.deleteByUserAndChapter(user, chapter);

      verify(readingProgressRepository).deleteByUserIdAndChapterId(userId, chapterId);
    }

    @Test
    @DisplayName("deve lidar com deleção quando nenhum progresso existe")
    void shouldHandleDeletionWhenNoProgressExists() {
      var newUser = User.builder().id(UUID.randomUUID()).build();
      var newChapter = Chapter.builder().id(UUID.randomUUID()).build();

      readingProgressService.deleteByUserAndChapter(newUser, newChapter);

      verify(readingProgressRepository).deleteByUserIdAndChapterId(newUser.getId(), newChapter.getId());
    }
  }

  @Nested
  @DisplayName("markAllAsRead()")
  class MarkAllAsReadTests {

    @Test
    @DisplayName("deve marcar todos os capítulos como lidos")
    void shouldMarkAllChaptersAsRead() {
      var chapter1 = Chapter.builder().id(UUID.randomUUID()).build();
      var chapter2 = Chapter.builder().id(UUID.randomUUID()).build();
      var chapters = List.of(chapter1, chapter2);

      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapter1.getId()))
          .thenReturn(Optional.empty());
      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapter2.getId()))
          .thenReturn(Optional.empty());
      when(readingProgressRepository.save(any(ReadingProgress.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      readingProgressService.markAllAsRead(user, chapters);

      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapter1.getId());
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapter2.getId());
      verify(readingProgressRepository, times(2)).save(any(ReadingProgress.class));
    }

    @Test
    @DisplayName("deve ignorar capítulos já marcados como lidos")
    void shouldIgnoreAlreadyReadChapters() {
      var chapter1 = Chapter.builder().id(UUID.randomUUID()).build();
      var chapter2 = Chapter.builder().id(UUID.randomUUID()).build();
      var chapters = List.of(chapter1, chapter2);
      var existingProgress = ReadingProgress.builder().user(user).chapter(chapter1).build();

      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapter1.getId()))
          .thenReturn(Optional.of(existingProgress));
      when(readingProgressRepository.findByUser_IdAndChapter_Id(userId, chapter2.getId()))
          .thenReturn(Optional.empty());
      when(readingProgressRepository.save(any(ReadingProgress.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      readingProgressService.markAllAsRead(user, chapters);

      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapter1.getId());
      verify(readingProgressRepository).findByUser_IdAndChapter_Id(userId, chapter2.getId());
      verify(readingProgressRepository).save(any(ReadingProgress.class));
    }

    @Test
    @DisplayName("deve lidar com lista vazia de capítulos")
    void shouldHandleEmptyChapterList() {
      readingProgressService.markAllAsRead(user, List.of());

      verify(readingProgressRepository, never()).findByUser_IdAndChapter_Id(any(), any());
      verify(readingProgressRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("unmarkAllByWorkId()")
  class UnmarkAllByWorkIdTests {

    @Test
    @DisplayName("deve desmarcar todos os capítulos de uma obra")
    void shouldUnmarkAllChaptersOfWork() {
      readingProgressService.unmarkAllByWorkId(user, workId);

      verify(readingProgressRepository).deleteAllByUserIdAndWorkId(userId, workId);
    }

    @Test
    @DisplayName("deve lidar com deleção quando nenhum progresso existe para a obra")
    void shouldHandleDeletionWhenNoProgressForWork() {
      var newWorkId = UUID.randomUUID();

      readingProgressService.unmarkAllByWorkId(user, newWorkId);

      verify(readingProgressRepository).deleteAllByUserIdAndWorkId(userId, newWorkId);
    }
  }

  @Nested
  @DisplayName("hasReadAnyChapter()")
  class HasReadAnyChapterTests {

    @Test
    @DisplayName("deve retornar verdadeiro quando usuário leu algum capítulo da obra")
    void shouldReturnTrueWhenUserReadAnyChapter() {
      when(readingProgressRepository.existsByUser_IdAndChapter_Work_Id(userId, workId))
          .thenReturn(true);

      var result = readingProgressService.hasReadAnyChapter(user, workId);

      assertThat(result).isTrue();
      verify(readingProgressRepository).existsByUser_IdAndChapter_Work_Id(userId, workId);
    }

    @Test
    @DisplayName("deve retornar falso quando usuário não leu nenhum capítulo da obra")
    void shouldReturnFalseWhenUserHasNotReadAnyChapter() {
      when(readingProgressRepository.existsByUser_IdAndChapter_Work_Id(userId, workId))
          .thenReturn(false);

      var result = readingProgressService.hasReadAnyChapter(user, workId);

      assertThat(result).isFalse();
      verify(readingProgressRepository).existsByUser_IdAndChapter_Work_Id(userId, workId);
    }
  }

  @Nested
  @DisplayName("findAllByUserAndChapterIds()")
  class FindAllByUserAndChapterIdsTests {

    @Test
    @DisplayName("deve retornar mapa de progressos para uma lista de capítulos")
    void shouldReturnMapOfProgressForChapterIds() {
      var chapter1Id = UUID.randomUUID();
      var chapter2Id = UUID.randomUUID();
      var chapterIds = List.of(chapter1Id, chapter2Id);

      var chapter1 = Chapter.builder().id(chapter1Id).build();
      var chapter2 = Chapter.builder().id(chapter2Id).build();
      var progress1 = ReadingProgress.builder().user(user).chapter(chapter1).build();
      var progress2 = ReadingProgress.builder().user(user).chapter(chapter2).build();
      var progressList = List.of(progress1, progress2);

      when(readingProgressRepository.findAllByUserIdAndChapterIdIn(userId, chapterIds))
          .thenReturn(progressList);

      var result = readingProgressService.findAllByUserAndChapterIds(user, chapterIds);

      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .containsKeys(chapter1Id, chapter2Id)
          .containsValues(progress1, progress2);
      verify(readingProgressRepository).findAllByUserIdAndChapterIdIn(userId, chapterIds);
    }

    @Test
    @DisplayName("deve retornar mapa vazio quando nenhum progresso encontrado")
    void shouldReturnEmptyMapWhenNoProgressFound() {
      var chapterIds = List.of(UUID.randomUUID(), UUID.randomUUID());

      when(readingProgressRepository.findAllByUserIdAndChapterIdIn(userId, chapterIds))
          .thenReturn(List.of());

      var result = readingProgressService.findAllByUserAndChapterIds(user, chapterIds);

      assertThat(result).isEmpty();
      verify(readingProgressRepository).findAllByUserIdAndChapterIdIn(userId, chapterIds);
    }

    @Test
    @DisplayName("deve retornar mapa vazio quando lista de capítulos é nula")
    void shouldReturnEmptyMapWhenChapterIdsIsNull() {
      var result = readingProgressService.findAllByUserAndChapterIds(user, null);

      assertThat(result).isEmpty();
      verify(readingProgressRepository, never()).findAllByUserIdAndChapterIdIn(any(), any());
    }

    @Test
    @DisplayName("deve retornar mapa vazio quando lista de capítulos está vazia")
    void shouldReturnEmptyMapWhenChapterIdsIsEmpty() {
      var result = readingProgressService.findAllByUserAndChapterIds(user, List.of());

      assertThat(result).isEmpty();
      verify(readingProgressRepository, never()).findAllByUserIdAndChapterIdIn(any(), any());
    }
  }
}
