package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_LoginDTO {
  
  private String email;
  private String senha;
}