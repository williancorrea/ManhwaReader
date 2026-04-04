package dev.williancorrea.manhwa.reader.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "secret";
    private Duration accessTokenExpiration = Duration.ofMinutes(15);
    private Duration refreshTokenExpiration = Duration.ofDays(7);
}
