package dev.williancorrea.manhwa.reader.scraper.mangotoons.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MangoEncryptedFeignConfig {

  @Bean
  public Decoder mangoDecoder(ObjectMapper mapper) {
    return new MangoDecoder(mapper);
  }

}