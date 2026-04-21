package dev.williancorrea.manhwa.reader.exception.custom;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundExceptionTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateWithMessageKeyAndArgs() {
      String messageKey = "exception.not-found";
      Object[] messageArgs = {"resource"};

      NotFoundException exception = new NotFoundException(messageKey, messageArgs);

      assertThat(exception).hasMessageContaining(messageKey);
      assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateWithEmptyArgs() {
      String messageKey = "exception.not-found";
      Object[] messageArgs = {};

      NotFoundException exception = new NotFoundException(messageKey, messageArgs);

      assertThat(exception).hasMessageContaining(messageKey);
    }
  }

  @Nested
  class GetMessageKey {

    @Test
    void shouldReturnMessageKey() {
      String messageKey = "exception.not-found.message";
      NotFoundException exception = new NotFoundException(messageKey, new Object[]{});

      assertThat(exception.getMessageKey()).isEqualTo(messageKey);
    }
  }

  @Nested
  class GetMessageArgs {

    @Test
    void shouldReturnMessageArgsArray() {
      Object[] messageArgs = {"id", 123};
      NotFoundException exception = new NotFoundException("key", messageArgs);

      assertThat(exception.getMessageArgs()).isEqualTo(messageArgs);
    }

    @Test
    void shouldReturnNullOrEmptyArgs() {
      Object[] messageArgs = {null, ""};
      NotFoundException exception = new NotFoundException("key", messageArgs);

      assertThat(exception.getMessageArgs()).contains(null, "");
    }
  }

  @Nested
  class ExceptionHierarchy {

    @Test
    void shouldExtendsRuntimeException() {
      NotFoundException exception = new NotFoundException("key", new Object[]{});

      assertThat(exception).isInstanceOf(RuntimeException.class);
      assertThat(exception).isInstanceOf(Exception.class);
      assertThat(exception).isInstanceOf(Throwable.class);
    }
  }
}
