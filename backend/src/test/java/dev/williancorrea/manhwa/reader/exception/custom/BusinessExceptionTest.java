package dev.williancorrea.manhwa.reader.exception.custom;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateWithMessageKeyAndArgs() {
      String messageKey = "exception.business.error";
      Object[] messageArgs = {"arg1", "arg2"};

      BusinessException exception = new BusinessException(messageKey, messageArgs);

      assertThat(exception).hasMessageContaining(messageKey);
      assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateWithMessageKeyArgsAndCause() {
      String messageKey = "exception.business.error";
      Object[] messageArgs = {"arg1"};
      Throwable cause = new IllegalArgumentException("Cause message");

      BusinessException exception = new BusinessException(messageKey, messageArgs, cause);

      assertThat(exception).hasMessageContaining(messageKey);
      assertThat(exception).hasCause(cause);
    }
  }

  @Nested
  class GetMessageKey {

    @Test
    void shouldReturnMessageKey() {
      String messageKey = "exception.custom.key";
      BusinessException exception = new BusinessException(messageKey, new Object[]{});

      assertThat(exception.getMessageKey()).isEqualTo(messageKey);
    }
  }

  @Nested
  class GetMessageArgs {

    @Test
    void shouldReturnMessageArgsArray() {
      Object[] messageArgs = {"value1", "value2", 123};
      BusinessException exception = new BusinessException("key", messageArgs);

      assertThat(exception.getMessageArgs()).isEqualTo(messageArgs);
    }

    @Test
    void shouldReturnEmptyArgsWhenNoneProvided() {
      Object[] messageArgs = {};
      BusinessException exception = new BusinessException("key", messageArgs);

      assertThat(exception.getMessageArgs()).isEmpty();
    }
  }

  @Nested
  class ExceptionHierarchy {

    @Test
    void shouldExtendsRuntimeException() {
      BusinessException exception = new BusinessException("key", new Object[]{});

      assertThat(exception).isInstanceOf(RuntimeException.class);
      assertThat(exception).isInstanceOf(Exception.class);
      assertThat(exception).isInstanceOf(Throwable.class);
    }
  }
}
