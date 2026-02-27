package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_TokenDTO {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
    private String refreshToken;
}