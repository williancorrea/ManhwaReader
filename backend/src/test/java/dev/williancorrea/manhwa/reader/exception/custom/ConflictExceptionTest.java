package dev.williancorrea.manhwa.reader.exception.custom;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConflictExceptionTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateWithMessageKeyAndArgs() {
      String messageKey = "exception.conflict";
      Object[] messageArgs = {"duplicate"};

      ConflictException exception = new ConflictException(messageKey, messageArgs);

      assertThat(exception).hasMessageContaining(messageKey);
      assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateWithoutArgs() {
      String messageKey = "exception.conflict.error";
      Object[] messageArgs = {};

      ConflictException exception = new ConflictException(messageKey, messageArgs);

      assertThat(exception).hasMessageContaining(messageKey);
    }
  }

  @Nested
  class GetMessageKey {

    @Test
    void shouldReturnMessageKey() {
      String messageKey = "conflict.key";
      ConflictException exception = new ConflictException(messageKey, new Object[]{});

      assertThat(exception.getMessageKey()).isEqualTo(messageKey);
    }
  }

  @Nested
  class GetMessageArgs {

    @Test
    void shouldReturnMessageArgsArray() {
      Object[] messageArgs = {"conflict_value"};
      ConflictException exception = new ConflictException("key", messageArgs);

      assertThat(exception.getMessageArgs()).isEqualTo(messageArgs);
    }

    @Test
    void shouldReturnMultipleArgs() {
      Object[] messageArgs = {"value1", "value2", "value3"};
      ConflictException exception = new ConflictException("key", messageArgs);

      assertThat(exception.getMessageArgs()).hasSize(3);
      assertThat(exception.getMessageArgs()).containsExactly("value1", "value2", "value3");
    }
  }

  @Nested
  class ExceptionHierarchy {

    @Test
    void shouldExtendsRuntimeException() {
      ConflictException exception = new ConflictException("key", new Object[]{});

      assertThat(exception).isInstanceOf(RuntimeException.class);
      assertThat(exception).isInstanceOf(Exception.class);
      assertThat(exception).isInstanceOf(Throwable.class);
    }
  }
}
