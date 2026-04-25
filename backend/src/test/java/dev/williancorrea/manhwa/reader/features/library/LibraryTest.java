package dev.williancorrea.manhwa.reader.features.library;

import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.work.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Library")
class LibraryTest {

  private UUID libraryId;
  private UUID userId;
  private UUID workId;
  private User user;
  private Work work;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    userId = UUID.randomUUID();
    workId = UUID.randomUUID();

    user = User.builder()
        .id(userId)
        .name("Test User")
        .email("test@example.com")
        .build();

    work = Work.builder()
        .id(workId)
        .slug("test-slug")
        .build();
  }

  @Nested
  @DisplayName("Builder")
  class BuilderTests {

    @Test
    @DisplayName("should build library with all fields")
    void shouldBuildLibraryWithAllFields() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library)
          .isNotNull()
          .satisfies(l -> {
            assertThat(l.getId()).isEqualTo(libraryId);
            assertThat(l.getUser()).isEqualTo(user);
            assertThat(l.getWork()).isEqualTo(work);
            assertThat(l.getStatus()).isEqualTo(LibraryStatus.READING);
          });
    }

    @Test
    @DisplayName("should build library without id")
    void shouldBuildLibraryWithoutId() {
      var library = Library.builder()
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library.getId()).isNull();
      assertThat(library.getUser()).isEqualTo(user);
      assertThat(library.getWork()).isEqualTo(work);
    }

    @Test
    @DisplayName("should build library with different statuses")
    void shouldBuildLibraryWithDifferentStatuses() {
      for (var status : LibraryStatus.values()) {
        var library = Library.builder()
            .id(UUID.randomUUID())
            .user(user)
            .work(work)
            .status(status)
            .build();

        assertThat(library.getStatus()).isEqualTo(status);
      }
    }
  }

  @Nested
  @DisplayName("Setters")
  class SetterTests {

    @Test
    @DisplayName("should set id")
    void shouldSetId() {
      var library = new Library();
      var newId = UUID.randomUUID();

      library.setId(newId);

      assertThat(library.getId()).isEqualTo(newId);
    }

    @Test
    @DisplayName("should set user")
    void shouldSetUser() {
      var library = new Library();
      var newUser = User.builder()
          .id(UUID.randomUUID())
          .name("New User")
          .email("new@example.com")
          .build();

      library.setUser(newUser);

      assertThat(library.getUser()).isEqualTo(newUser);
    }

    @Test
    @DisplayName("should set work")
    void shouldSetWork() {
      var library = new Library();
      var newWork = Work.builder()
          .id(UUID.randomUUID())
          .slug("new-slug")
          .build();

      library.setWork(newWork);

      assertThat(library.getWork()).isEqualTo(newWork);
    }

    @Test
    @DisplayName("should set status")
    void shouldSetStatus() {
      var library = new Library();

      library.setStatus(LibraryStatus.COMPLETED);

      assertThat(library.getStatus()).isEqualTo(LibraryStatus.COMPLETED);
    }

    @Test
    @DisplayName("should update status")
    void shouldUpdateStatus() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      library.setStatus(LibraryStatus.DROPPED);

      assertThat(library.getStatus()).isEqualTo(LibraryStatus.DROPPED);
    }
  }

  @Nested
  @DisplayName("Getters")
  class GetterTests {

    @Test
    @DisplayName("should return id")
    void shouldReturnId() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library.getId()).isEqualTo(libraryId);
    }

    @Test
    @DisplayName("should return user")
    void shouldReturnUser() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("should return work")
    void shouldReturnWork() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library.getWork()).isEqualTo(work);
    }

    @Test
    @DisplayName("should return status")
    void shouldReturnStatus() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library.getStatus()).isEqualTo(LibraryStatus.READING);
    }
  }

  @Nested
  @DisplayName("Equality and Hash")
  class EqualityTests {

    @Test
    @DisplayName("should be equal when same id")
    void shouldBeEqualWhenSameId() {
      var library1 = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      var library2 = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library1).isEqualTo(library2);
    }

    @Test
    @DisplayName("should not be equal when different id")
    void shouldNotBeEqualWhenDifferentId() {
      var library1 = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      var library2 = Library.builder()
          .id(UUID.randomUUID())
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library1).isNotEqualTo(library2);
    }

    @Test
    @DisplayName("should have same hash code when equal")
    void shouldHaveSameHashCodeWhenEqual() {
      var library1 = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      var library2 = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library1.hashCode()).isEqualTo(library2.hashCode());
    }
  }

  @Nested
  @DisplayName("Serializable")
  class SerializableTests {

    @Test
    @DisplayName("should be serializable")
    void shouldBeSerializable() {
      var library = Library.builder()
          .id(libraryId)
          .user(user)
          .work(work)
          .status(LibraryStatus.READING)
          .build();

      assertThat(library).isInstanceOf(java.io.Serializable.class);
    }
  }

  @Nested
  @DisplayName("No Args Constructor")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty library instance")
    void shouldCreateEmptyLibraryInstance() {
      var library = new Library();

      assertThat(library.getId()).isNull();
      assertThat(library.getUser()).isNull();
      assertThat(library.getWork()).isNull();
      assertThat(library.getStatus()).isNull();
    }
  }

  @Nested
  @DisplayName("All Args Constructor")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create library with all args")
    void shouldCreateLibraryWithAllArgs() {
      var library = new Library(libraryId, user, work, LibraryStatus.READING);

      assertThat(library.getId()).isEqualTo(libraryId);
      assertThat(library.getUser()).isEqualTo(user);
      assertThat(library.getWork()).isEqualTo(work);
      assertThat(library.getStatus()).isEqualTo(LibraryStatus.READING);
    }

    @Test
    @DisplayName("should create library with null values")
    void shouldCreateLibraryWithNullValues() {
      var library = new Library(null, null, null, null);

      assertThat(library.getId()).isNull();
      assertThat(library.getUser()).isNull();
      assertThat(library.getWork()).isNull();
      assertThat(library.getStatus()).isNull();
    }
  }
}
