package dev.williancorrea.manhwa.reader.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth.cookie")
public class CookieProperties {

    private boolean secure = true;
    private String sameSite = "Strict";
}
