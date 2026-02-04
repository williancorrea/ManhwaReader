package dev.williancorrea.manhwa.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"dev.williancorrea.manhwa.reader"})
public class ReaderApplication {

  private ReaderApplication() {
  }

  static void main(String[] args) {
    SpringApplication.run(ReaderApplication.class, args);
  }

}
