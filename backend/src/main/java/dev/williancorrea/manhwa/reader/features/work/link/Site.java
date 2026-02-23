package dev.williancorrea.manhwa.reader.features.work.link;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Site implements Serializable {

  @Serial
  private static final long serialVersionUID = -963924079398936408L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private String url;
}
