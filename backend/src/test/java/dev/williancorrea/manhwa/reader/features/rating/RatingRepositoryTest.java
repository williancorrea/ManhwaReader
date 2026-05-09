package dev.williancorrea.manhwa.reader.features.rating;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingRepository")
class RatingRepositoryTest {

  @Mock
  private RatingRepository ratingRepository;

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
  @DisplayName("findByUser_IdAndWork_Id()")
  class FindByUserIdAndWorkIdTests {

    @Test
    @DisplayName("should find rating by user id and work id")
    void shouldFindRatingByUserIdAndWorkId() {
      when(ratingRepository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(rating));

      var result = ratingRepository.findByUser_IdAndWork_Id(userId, workId);

      assertThat(result)
          .isPresent()
          .contains(rating);
      verify(ratingRepository).findByUser_IdAndWork_Id(userId, workId);
    }

    @Test
    @DisplayName("should return empty optional when rating not found")
    void shouldReturnEmptyOptionalWhenRatingNotFound() {
      var nonExistentUserId = UUID.randomUUID();
      var nonExistentWorkId = UUID.randomUUID();

      when(ratingRepository.findByUser_IdAndWork_Id(nonExistentUserId, nonExistentWorkId))
          .thenReturn(Optional.empty());

      var result = ratingRepository.findByUser_IdAndWork_Id(nonExistentUserId, nonExistentWorkId);

      assertThat(result).isEmpty();
      verify(ratingRepository).findByUser_IdAndWork_Id(nonExistentUserId, nonExistentWorkId);
    }

    @Test
    @DisplayName("should return empty optional for null user id")
    void shouldReturnEmptyOptionalForNullUserId() {
      when(ratingRepository.findByUser_IdAndWork_Id(null, workId))
          .thenReturn(Optional.empty());

      var result = ratingRepository.findByUser_IdAndWork_Id(null, workId);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return empty optional for null work id")
    void shouldReturnEmptyOptionalForNullWorkId() {
      when(ratingRepository.findByUser_IdAndWork_Id(userId, null))
          .thenReturn(Optional.empty());

      var result = ratingRepository.findByUser_IdAndWork_Id(userId, null);

      assertThat(result).isEmpty();
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

      when(ratingRepository.findByUser_IdAndWork_Id(userId, workId))
          .thenReturn(Optional.of(highScoreRating));

      var result = ratingRepository.findByUser_IdAndWork_Id(userId, workId);

      assertThat(result)
          .isPresent()
          .get()
          .extracting("score")
          .isEqualTo(10);
    }
  }

  @Nested
  @DisplayName("JpaRepository standard methods")
  class JpaRepositoryTests {

    @Test
    @DisplayName("should find rating by id")
    void shouldFindRatingById() {
      when(ratingRepository.findById(ratingId))
          .thenReturn(Optional.of(rating));

      var result = ratingRepository.findById(ratingId);

      assertThat(result)
          .isPresent()
          .contains(rating);
      verify(ratingRepository).findById(ratingId);
    }

    @Test
    @DisplayName("should return empty optional when rating id not found")
    void shouldReturnEmptyOptionalWhenRatingIdNotFound() {
      var nonExistentId = UUID.randomUUID();

      when(ratingRepository.findById(nonExistentId))
          .thenReturn(Optional.empty());

      var result = ratingRepository.findById(nonExistentId);

      assertThat(result).isEmpty();
      verify(ratingRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("should save rating")
    void shouldSaveRating() {
      when(ratingRepository.save(any(Rating.class)))
          .thenReturn(rating);

      var result = ratingRepository.save(rating);

      assertThat(result).isEqualTo(rating);
      verify(ratingRepository).save(rating);
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

      when(ratingRepository.save(newRating))
          .thenReturn(rating);

      var result = ratingRepository.save(newRating);

      assertThat(result.getId()).isNotNull();
      verify(ratingRepository).save(newRating);
    }

    @Test
    @DisplayName("should find all ratings")
    void shouldFindAllRatings() {
      var ratings = List.of(rating);

      when(ratingRepository.findAll())
          .thenReturn(ratings);

      var result = ratingRepository.findAll();

      assertThat(result)
          .hasSize(1)
          .contains(rating);
      verify(ratingRepository).findAll();
    }

    @Test
    @DisplayName("should find multiple ratings")
    void shouldFindMultipleRatings() {
      var secondRating = Rating.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(Work.builder().id(UUID.randomUUID()).build())
          .score(9)
          .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
          .build();

      var ratings = List.of(rating, secondRating);

      when(ratingRepository.findAll())
          .thenReturn(ratings);

      var result = ratingRepository.findAll();

      assertThat(result)
          .hasSize(2)
          .contains(rating, secondRating);
      verify(ratingRepository).findAll();
    }

    @Test
    @DisplayName("should delete rating by id")
    void shouldDeleteRatingById() {
      ratingRepository.deleteById(ratingId);

      verify(ratingRepository).deleteById(ratingId);
    }

    @Test
    @DisplayName("should check if rating exists")
    void shouldCheckIfRatingExists() {
      when(ratingRepository.existsById(ratingId))
          .thenReturn(true);

      var result = ratingRepository.existsById(ratingId);

      assertThat(result).isTrue();
      verify(ratingRepository).existsById(ratingId);
    }

    @Test
    @DisplayName("should return false when checking if non-existent rating exists")
    void shouldReturnFalseWhenCheckingIfNonExistentRatingExists() {
      var nonExistentId = UUID.randomUUID();

      when(ratingRepository.existsById(nonExistentId))
          .thenReturn(false);

      var result = ratingRepository.existsById(nonExistentId);

      assertThat(result).isFalse();
      verify(ratingRepository).existsById(nonExistentId);
    }

    @Test
    @DisplayName("should count all ratings")
    void shouldCountAllRatings() {
      when(ratingRepository.count())
          .thenReturn(1L);

      var result = ratingRepository.count();

      assertThat(result).isEqualTo(1L);
      verify(ratingRepository).count();
    }

    @Test
    @DisplayName("should count multiple ratings")
    void shouldCountMultipleRatings() {
      when(ratingRepository.count())
          .thenReturn(5L);

      var result = ratingRepository.count();

      assertThat(result).isEqualTo(5L);
      verify(ratingRepository).count();
    }
  }
}
