package dev.williancorrea.manhwa.reader.features.rating;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService")
class RatingServiceTest {

  @Mock
  private RatingRepository repository;

  @InjectMocks
  private RatingService service;

  private UUID ratingId;
  private UUID userId;
  private UUID workId;
  private User user;
  private Work work;
  private Rating rating;

  @BeforeEach
  void setUp() {
    ratingId = UUID.randomUUID();
    userId = UUID.randomUUID();
    workId = UUID.randomUUID();
    user = User.builder().id(userId).build();
    work = Work.builder().id(workId).build();
    rating = Rating.builder()
        .id(ratingId)
        .user(user)
        .work(work)
        .score(8)
        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
        .build();
  }

  @Nested
  @DisplayName("findAll()")
  class FindAllTests {

    @Test
    @DisplayName("should return all ratings")
    void shouldReturnAllRatings() {
      var ratings = List.of(rating);

      when(repository.findAll()).thenReturn(ratings);

      var result = service.findAll();

      assertThat(result)
          .hasSize(1)
          .contains(rating);
      verify(repository).findAll();
    }

    @Test
    @DisplayName("should return empty list when no ratings exist")
    void shouldReturnEmptyListWhenNoRatingsExist() {
      when(repository.findAll()).thenReturn(List.of());

      var result = service.findAll();

      assertThat(result).isEmpty();
      verify(repository).findAll();
    }

    @Test
    @DisplayName("should return multiple ratings")
    void shouldReturnMultipleRatings() {
      var secondRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(Work.builder().id(UUID.randomUUID()).build())
          .score(9)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var ratings = List.of(rating, secondRating);

      when(repository.findAll()).thenReturn(ratings);

      var result = service.findAll();

      assertThat(result)
          .hasSize(2)
          .contains(rating, secondRating);
      verify(repository).findAll();
    }

    @Test
    @DisplayName("should return ratings with different scores")
    void shouldReturnRatingsWithDifferentScores() {
      var lowScoreRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(User.builder().id(UUID.randomUUID()).build())
          .work(work)
          .score(2)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var highScoreRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(User.builder().id(UUID.randomUUID()).build())
          .work(work)
          .score(10)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var ratings = List.of(lowScoreRating, highScoreRating);

      when(repository.findAll()).thenReturn(ratings);

      var result = service.findAll();

      assertThat(result)
          .hasSize(2)
          .extracting("score")
          .contains(2, 10);
      verify(repository).findAll();
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("should find rating by id")
    void shouldFindRatingById() {
      when(repository.findById(ratingId)).thenReturn(Optional.of(rating));

      var result = service.findById(ratingId);

      assertThat(result)
          .isPresent()
          .contains(rating);
      verify(repository).findById(ratingId);
    }

    @Test
    @DisplayName("should return empty optional when rating not found")
    void shouldReturnEmptyOptionalWhenRatingNotFound() {
      var nonExistentId = UUID.randomUUID();

      when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

      var result = service.findById(nonExistentId);

      assertThat(result).isEmpty();
      verify(repository).findById(nonExistentId);
    }

    @Test
    @DisplayName("should find rating with different ids")
    void shouldFindRatingWithDifferentIds() {
      var anotherRatingId = UUID.randomUUID();
      var anotherRating = Rating.builder()
          .id(anotherRatingId)
          .user(user)
          .work(work)
          .score(5)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.findById(anotherRatingId)).thenReturn(Optional.of(anotherRating));

      var result = service.findById(anotherRatingId);

      assertThat(result)
          .isPresent()
          .get()
          .extracting("id")
          .isEqualTo(anotherRatingId);
      verify(repository).findById(anotherRatingId);
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {

    @Test
    @DisplayName("should save rating")
    void shouldSaveRating() {
      when(repository.save(rating)).thenReturn(rating);

      var result = service.save(rating);

      assertThat(result)
          .isNotNull()
          .isEqualTo(rating);
      verify(repository).save(rating);
    }

    @Test
    @DisplayName("should save new rating and return with id")
    void shouldSaveNewRatingAndReturnWithId() {
      var newRating = Rating.builder()
          .user(user)
          .work(work)
          .score(7)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var savedRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .score(7)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(newRating)).thenReturn(savedRating);

      var result = service.save(newRating);

      assertThat(result.getId()).isNotNull();
      verify(repository).save(newRating);
    }

    @Test
    @DisplayName("should update existing rating")
    void shouldUpdateExistingRating() {
      var updatedRating = Rating.builder()
          .id(ratingId)
          .user(user)
          .work(work)
          .score(10)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(updatedRating)).thenReturn(updatedRating);

      var result = service.save(updatedRating);

      assertThat(result.getScore()).isEqualTo(10);
      verify(repository).save(updatedRating);
    }

    @Test
    @DisplayName("should save rating with different scores")
    void shouldSaveRatingWithDifferentScores() {
      var lowScoreRating = Rating.builder()
          .user(user)
          .work(work)
          .score(1)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(lowScoreRating)).thenReturn(lowScoreRating);

      var result = service.save(lowScoreRating);

      assertThat(result.getScore()).isEqualTo(1);
      verify(repository).save(lowScoreRating);
    }

    @Test
    @DisplayName("should save rating with updated score for same user and work")
    void shouldSaveRatingWithUpdatedScoreForSameUserAndWork() {
      var originalRating = Rating.builder()
          .id(ratingId)
          .user(user)
          .work(work)
          .score(5)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var updatedRating = Rating.builder()
          .id(ratingId)
          .user(user)
          .work(work)
          .score(8)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(updatedRating)).thenReturn(updatedRating);

      var result = service.save(updatedRating);

      assertThat(result.getScore()).isEqualTo(8);
      verify(repository).save(updatedRating);
    }
  }

  @Nested
  @DisplayName("existsById()")
  class ExistsByIdTests {

    @Test
    @DisplayName("should return true when rating exists")
    void shouldReturnTrueWhenRatingExists() {
      when(repository.existsById(ratingId)).thenReturn(true);

      var result = service.existsById(ratingId);

      assertThat(result).isTrue();
      verify(repository).existsById(ratingId);
    }

    @Test
    @DisplayName("should return false when rating does not exist")
    void shouldReturnFalseWhenRatingDoesNotExist() {
      var nonExistentId = UUID.randomUUID();

      when(repository.existsById(nonExistentId)).thenReturn(false);

      var result = service.existsById(nonExistentId);

      assertThat(result).isFalse();
      verify(repository).existsById(nonExistentId);
    }

    @Test
    @DisplayName("should check existence for multiple ids")
    void shouldCheckExistenceForMultipleIds() {
      var otherId = UUID.randomUUID();

      when(repository.existsById(ratingId)).thenReturn(true);
      when(repository.existsById(otherId)).thenReturn(false);

      assertThat(service.existsById(ratingId)).isTrue();
      assertThat(service.existsById(otherId)).isFalse();
      verify(repository).existsById(ratingId);
      verify(repository).existsById(otherId);
    }
  }

  @Nested
  @DisplayName("deleteById()")
  class DeleteByIdTests {

    @Test
    @DisplayName("should delete rating by id")
    void shouldDeleteRatingById() {
      service.deleteById(ratingId);

      verify(repository).deleteById(ratingId);
    }

    @Test
    @DisplayName("should handle deletion of non-existent rating")
    void shouldHandleDeletionOfNonExistentRating() {
      var nonExistentId = UUID.randomUUID();

      service.deleteById(nonExistentId);

      verify(repository).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("should delete multiple ratings")
    void shouldDeleteMultipleRatings() {
      var secondRatingId = UUID.randomUUID();

      service.deleteById(ratingId);
      service.deleteById(secondRatingId);

      verify(repository).deleteById(ratingId);
      verify(repository).deleteById(secondRatingId);
    }
  }

  @Nested
  @DisplayName("findByUserAndWork()")
  class FindByUserAndWorkTests {

    @Test
    @DisplayName("should find rating by user and work")
    void shouldFindRatingByUserAndWork() {
      when(repository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(rating));

      var result = service.findByUserAndWork(user, work);

      assertThat(result)
          .isPresent()
          .contains(rating);
      verify(repository).findByUser_IdAndWork_Id(userId, workId);
    }

    @Test
    @DisplayName("should return empty optional when rating not found")
    void shouldReturnEmptyOptionalWhenRatingNotFound() {
      var anotherUser = User.builder().id(UUID.randomUUID()).build();
      var anotherWork = Work.builder().id(UUID.randomUUID()).build();

      when(repository.findByUser_IdAndWork_Id(anotherUser.getId(), anotherWork.getId()))
          .thenReturn(Optional.empty());

      var result = service.findByUserAndWork(anotherUser, anotherWork);

      assertThat(result).isEmpty();
      verify(repository).findByUser_IdAndWork_Id(anotherUser.getId(), anotherWork.getId());
    }

    @Test
    @DisplayName("should find rating with different scores")
    void shouldFindRatingWithDifferentScores() {
      var highScoreRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .score(10)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(highScoreRating));

      var result = service.findByUserAndWork(user, work);

      assertThat(result)
          .isPresent()
          .get()
          .extracting("score")
          .isEqualTo(10);
      verify(repository).findByUser_IdAndWork_Id(userId, workId);
    }

    @Test
    @DisplayName("should find rating with same user but different works")
    void shouldFindRatingWithSameUserButDifferentWorks() {
      var anotherWorkId = UUID.randomUUID();
      var anotherWork = Work.builder().id(anotherWorkId).build();
      var ratingForAnotherWork = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(anotherWork)
          .score(7)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.findByUser_IdAndWork_Id(userId, anotherWorkId))
          .thenReturn(Optional.of(ratingForAnotherWork));

      var result = service.findByUserAndWork(user, anotherWork);

      assertThat(result)
          .isPresent()
          .get()
          .extracting("work.id")
          .isEqualTo(anotherWorkId);
      verify(repository).findByUser_IdAndWork_Id(userId, anotherWorkId);
    }

    @Test
    @DisplayName("should find rating with different users but same work")
    void shouldFindRatingWithDifferentUsersButSameWork() {
      var anotherUserId = UUID.randomUUID();
      var anotherUser = User.builder().id(anotherUserId).build();
      var ratingFromAnotherUser = Rating.builder()
          .id(UUID.randomUUID())
          .user(anotherUser)
          .work(work)
          .score(6)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.findByUser_IdAndWork_Id(anotherUserId, workId))
          .thenReturn(Optional.of(ratingFromAnotherUser));

      var result = service.findByUserAndWork(anotherUser, work);

      assertThat(result)
          .isPresent()
          .get()
          .extracting("user.id")
          .isEqualTo(anotherUserId);
      verify(repository).findByUser_IdAndWork_Id(anotherUserId, workId);
    }

    @Test
    @DisplayName("should use user and work ids for repository query")
    void shouldUseUserAndWorkIdsForRepositoryQuery() {
      when(repository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(rating));

      service.findByUserAndWork(user, work);

      verify(repository).findByUser_IdAndWork_Id(userId, workId);
    }

    @Test
    @DisplayName("should not call repository when finding non-existent rating")
    void shouldCallRepositoryEvenWhenFindingNonExistentRating() {
      var nonExistentUserId = UUID.randomUUID();
      var nonExistentWorkId = UUID.randomUUID();
      var nonExistentUser = User.builder().id(nonExistentUserId).build();
      var nonExistentWork = Work.builder().id(nonExistentWorkId).build();

      when(repository.findByUser_IdAndWork_Id(nonExistentUserId, nonExistentWorkId))
          .thenReturn(Optional.empty());

      var result = service.findByUserAndWork(nonExistentUser, nonExistentWork);

      assertThat(result).isEmpty();
      verify(repository).findByUser_IdAndWork_Id(nonExistentUserId, nonExistentWorkId);
    }

    @Test
    @DisplayName("should return rating with matching user and work")
    void shouldReturnRatingWithMatchingUserAndWork() {
      when(repository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(rating));

      var result = service.findByUserAndWork(user, work);

      assertThat(result)
          .isPresent()
          .get()
          .satisfies(r -> {
            assertThat(r.getUser().getId()).isEqualTo(userId);
            assertThat(r.getWork().getId()).isEqualTo(workId);
          });
    }
  }

  @Nested
  @DisplayName("Service integration scenarios")
  class IntegrationScenarios {

    @Test
    @DisplayName("should create new rating and find it")
    void shouldCreateNewRatingAndFindIt() {
      var newRating = Rating.builder()
          .user(user)
          .work(work)
          .score(5)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();
      var savedRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .score(5)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(newRating)).thenReturn(savedRating);
      when(repository.findById(savedRating.getId())).thenReturn(Optional.of(savedRating));

      var created = service.save(newRating);
      var found = service.findById(created.getId());

      assertThat(found)
          .isPresent()
          .contains(created);
      verify(repository).save(newRating);
      verify(repository).findById(savedRating.getId());
    }

    @Test
    @DisplayName("should update rating score and find updated version")
    void shouldUpdateRatingScoreAndFindUpdatedVersion() {
      var updatedRating = Rating.builder()
          .id(ratingId)
          .user(user)
          .work(work)
          .score(9)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(updatedRating)).thenReturn(updatedRating);
      when(repository.findById(ratingId)).thenReturn(Optional.of(updatedRating));

      var saved = service.save(updatedRating);
      var found = service.findById(ratingId);

      assertThat(found)
          .isPresent()
          .get()
          .extracting("score")
          .isEqualTo(9);
    }

    @Test
    @DisplayName("should create rating and verify it exists before deletion")
    void shouldCreateRatingAndVerifyItExistsBeforeDeletion() {
      var savedRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .score(8)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      when(repository.save(any(Rating.class))).thenReturn(savedRating);
      when(repository.existsById(savedRating.getId())).thenReturn(true);

      service.save(savedRating);
      var exists = service.existsById(savedRating.getId());

      assertThat(exists).isTrue();
      verify(repository).save(any(Rating.class));
      verify(repository).existsById(savedRating.getId());
    }
  }
}
