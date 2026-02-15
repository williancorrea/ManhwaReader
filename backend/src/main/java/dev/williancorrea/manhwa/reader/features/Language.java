package dev.williancorrea.manhwa.reader.features;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "language")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Language implements Serializable {

  @Serial
  private static final long serialVersionUID = -7262141750723667515L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Size(min = 5, max = 10)
  private String code;

  @Size(min = 3, max = 50)
  private String name;
}
