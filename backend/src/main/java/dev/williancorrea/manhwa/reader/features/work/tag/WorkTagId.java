package dev.williancorrea.manhwa.reader.features.work.tag;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkTagId implements Serializable {
  private UUID work;
  private UUID tag;
}
