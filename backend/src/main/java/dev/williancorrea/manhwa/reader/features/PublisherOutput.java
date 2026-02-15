package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublisherOutput implements Serializable {
  private UUID id;
  private String name;

  public PublisherOutput(Publisher publisher) {
    this.id = publisher.getId();
    this.name = publisher.getName();
  }
}
