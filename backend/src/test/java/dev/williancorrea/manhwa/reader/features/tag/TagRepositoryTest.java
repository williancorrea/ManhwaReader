package dev.williancorrea.manhwa.reader.features.tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagRepository")
class TagRepositoryTest {

  @Nested
  @DisplayName("findByGroupAndName()")
  class FindByGroupAndNameTests {

    @Test
    @DisplayName("should define interface with findByGroupAndName method")
    void shouldDefineInterfaceWithFindByGroupAndNameMethod() {
      assertThat(TagRepository.class).isInterface();
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
    }

    @Test
    @DisplayName("should have query annotation on findByGroupAndName")
    void shouldHaveQueryAnnotationOnFindByGroupAndName() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      assertThat(method.get().getAnnotation(org.springframework.data.jpa.repository.Query.class))
          .isNotNull();
    }

    @Test
    @DisplayName("should accept group and name parameters")
    void shouldAcceptGroupAndNameParameters() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      assertThat(method.get().getParameterCount()).isEqualTo(2);
      var params = method.get().getParameters();
      assertThat(params[0].getType()).isEqualTo(String.class);
      assertThat(params[1].getType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("should return Optional<Tag>")
    void shouldReturnOptionalTag() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      var returnType = method.get().getReturnType();
      assertThat(returnType).isEqualTo(java.util.Optional.class);
    }

    @Test
    @DisplayName("should use case-insensitive query for group")
    void shouldUseCaseInsensitiveQueryForGroup() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      var query = method.get().getAnnotation(org.springframework.data.jpa.repository.Query.class);
      assertThat(query.value())
          .contains("lower(t.group_tag) = lower(:group)");
    }

    @Test
    @DisplayName("should search by name and aliases case-insensitively")
    void shouldSearchByNameAndAliasesCaseInsensitively() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      var query = method.get().getAnnotation(org.springframework.data.jpa.repository.Query.class);
      assertThat(query.value())
          .contains("lower(t.name) = lower(:name)")
          .contains("lower(t.alias1) = lower(:name)")
          .contains("lower(t.alias2) = lower(:name)")
          .contains("lower(t.alias3) = lower(:name)");
    }

    @Test
    @DisplayName("should use limit in query")
    void shouldUseLimitInQuery() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      var query = method.get().getAnnotation(org.springframework.data.jpa.repository.Query.class);
      assertThat(query.value()).contains("limit 1");
    }

    @Test
    @DisplayName("should use native query")
    void shouldUseNativeQuery() {
      var method = java.util.Arrays.stream(TagRepository.class.getDeclaredMethods())
          .filter(m -> m.getName().equals("findByGroupAndName"))
          .findFirst();
      assertThat(method).isPresent();
      var query = method.get().getAnnotation(org.springframework.data.jpa.repository.Query.class);
      assertThat(query.nativeQuery()).isTrue();
    }
  }
}
