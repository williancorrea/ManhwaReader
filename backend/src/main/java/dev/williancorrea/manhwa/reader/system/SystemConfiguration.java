package dev.williancorrea.manhwa.reader.system;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_configuration")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfiguration implements Serializable {

  @Serial
  private static final long serialVersionUID = 5630844365065411285L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_system_configuration_group", nullable = false)
  private SystemConfigurationGroup group;

  @Column(name = "description", length = 200)
  @Size(max = 200)
  private String description;

  @Column(name = "reference", length = 200, nullable = false, unique = true)
  @Size(max = 200)
  private String reference;

  @Column(name = "value", length = 200, nullable = false)
  @Size(max = 200)
  private String value;

  @Column(nullable = false)
  private boolean active;

}
