package dev.williancorrea.manhwa.reader.features.scanlator;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.synchronization.SynchronizationOriginType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scanlator")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scanlator implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotEmpty
  @NotNull
  @NotBlank
  @Size(min = 2, max = 255)
  @Column(nullable = false)
  private String name;

  private String website;

  @NotEmpty
  @NotNull
  @NotBlank
  @Size(max = 20)
  @Column(nullable = false, unique = true)
  private String code;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SynchronizationOriginType synchronization;
}
