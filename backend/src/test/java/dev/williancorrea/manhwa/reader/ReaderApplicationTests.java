package dev.williancorrea.manhwa.reader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReaderApplicationTests {

  @Test
  void Workaround_for_nested_class_coverage() {
    assertTrue(true);
  }

  @Nested
  class MainMethodTests {

    @Test
    void givenApplicationWhenMainMethodCalledThenSpringApplicationRuns() {
      try (var mockSpringApplication = mockStatic(SpringApplication.class)) {
        dev.williancorrea.manhwa.reader.ReaderApplication.main(new String[] {});
        mockSpringApplication.verify(
            () -> SpringApplication.run(dev.williancorrea.manhwa.reader.ReaderApplication.class, new String[] {})
        );
      }
    }
  }

}
