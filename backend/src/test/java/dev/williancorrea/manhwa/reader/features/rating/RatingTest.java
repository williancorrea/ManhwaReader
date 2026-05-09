package dev.williancorrea.manhwa.reader.features.rating;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rating")
class RatingTest {

  @Nested
  @DisplayName("Entity construction")
  class EntityConstructionTests {

    @Test
    @DisplayName("should create rating with builder")
    void shouldCreateRatingWithBuilder() {
      var id = UUID.randomUUID();
      var userId = UUID.randomUUID();
      var workId = UUID.randomUUID();
      var user = User.builder().id(userId).build();
      var work = Work.builder().id(workId).build();
      var score = 8;
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      var rating = Rating.builder()
          .id(id)
          .user(user)
          .work(work)
          .score(score)
          .createdAt(createdAt)
          .build();

      assertThat(rating.getId()).isEqualTo(id);
      assertThat(rating.getUser()).isEqualTo(user);
      assertThat(rating.getWork()).isEqualTo(work);
      assertThat(rating.getScore()).isEqualTo(score);
      assertThat(rating.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should create rating with minimal fields")
    void shouldCreateRatingWithMinimalFields() {
      var userId = UUID.randomUUID();
      var workId = UUID.randomUUID();
      var user = User.builder().id(userId).build();
      var work = Work.builder().id(workId).build();
      var score = 5;

      var rating = Rating.builder()
          .user(user)
          .work(work)
          .score(score)
          .build();

      assertThat(rating.getId()).isNull();
      assertThat(rating.getUser()).isEqualTo(user);
      assertThat(rating.getWork()).isEqualTo(work);
      assertThat(rating.getScore()).isEqualTo(score);
      assertThat(rating.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("should create rating with no-arg constructor")
    void shouldCreateRatingWithNoArgConstructor() {
      var rating = new Rating();

      assertThat(rating.getId()).isNull();
      assertThat(rating.getUser()).isNull();
      assertThat(rating.getWork()).isNull();
      assertThat(rating.getScore()).isNull();
      assertThat(rating.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("should create rating with all-arg constructor")
    void shouldCreateRatingWithAllArgConstructor() {
      var id = UUID.randomUUID();
      var userId = UUID.randomUUID();
      var workId = UUID.randomUUID();
      var user = User.builder().id(userId).build();
      var work = Work.builder().id(workId).build();
      var score = 9;
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      var rating = new Rating(id, user, work, score, createdAt);

      assertThat(rating.getId()).isEqualTo(id);
      assertThat(rating.getUser()).isEqualTo(user);
      assertThat(rating.getWork()).isEqualTo(work);
      assertThat(rating.getScore()).isEqualTo(score);
      assertThat(rating.getCreatedAt()).isEqualTo(createdAt);
    }
  }

  @Nested
  @DisplayName("Setters and Getters")
  class SettersAndGettersTests {

    @Test
    @DisplayName("should set and get id")
    void shouldSetAndGetId() {
      var rating = new Rating();
      var id = UUID.randomUUID();

      rating.setId(id);

      assertThat(rating.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("should set and get user")
    void shouldSetAndGetUser() {
      var rating = new Rating();
      var userId = UUID.randomUUID();
      var user = User.builder().id(userId).build();

      rating.setUser(user);

      assertThat(rating.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("should set and get work")
    void shouldSetAndGetWork() {
      var rating = new Rating();
      var workId = UUID.randomUUID();
      var work = Work.builder().id(workId).build();

      rating.setWork(work);

      assertThat(rating.getWork()).isEqualTo(work);
    }

    @Test
    @DisplayName("should set and get score")
    void shouldSetAndGetScore() {
      var rating = new Rating();
      var score = 7;

      rating.setScore(score);

      assertThat(rating.getScore()).isEqualTo(score);
    }

    @Test
    @DisplayName("should set and get createdAt")
    void shouldSetAndGetCreatedAt() {
      var rating = new Rating();
      var createdAt = OffsetDateTime.now(ZoneOffset.UTC);

      rating.setCreatedAt(createdAt);

      assertThat(rating.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("should update score on existing rating")
    void shouldUpdateScoreOnExistingRating() {
      var id = UUID.randomUUID();
      var userId = UUID.randomUUID();
      var workId = UUID.randomUUID();
      var user = User.builder().id(userId).build();
      var work = Work.builder().id(workId).build();
      var rating = Rating.builder()
          .id(id)
          .user(user)
          .work(work)
          .score(5)
          .build();

      rating.setScore(10);

      assertThat(rating.getScore()).isEqualTo(10);
    }
  }

  @Nested
  @DisplayName("Score variations")
  class ScoreVariationsTests {

    @Test
    @DisplayName("should accept minimum score value")
    void shouldAcceptMinimumScoreValue() {
      var rating = Rating.builder()
          .score(1)
          .build();

      assertThat(rating.getScore()).isEqualTo(1);
    }

    @Test
    @DisplayName("should accept maximum score value")
    void shouldAcceptMaximumScoreValue() {
      var rating = Rating.builder()
          .score(10)
          .build();

      assertThat(rating.getScore()).isEqualTo(10);
    }

    @Test
    @DisplayName("should accept middle range score")
    void shouldAcceptMiddleRangeScore() {
      var rating = Rating.builder()
          .score(5)
          .build();

      assertThat(rating.getScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("should accept zero score")
    void shouldAcceptZeroScore() {
      var rating = Rating.builder()
          .score(0)
          .build();

      assertThat(rating.getScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("should accept negative score")
    void shouldAcceptNegativeScore() {
      var rating = Rating.builder()
          .score(-5)
          .build();

      assertThat(rating.getScore()).isEqualTo(-5);
    }
  }
}
