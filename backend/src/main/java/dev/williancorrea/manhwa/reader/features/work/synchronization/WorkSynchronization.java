package dev.williancorrea.manhwa.reader.features.work.synchronization;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.work.Work;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_synchronization")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkSynchronization implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  @NotNull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SynchronizationOriginType origin;

  @NotBlank
  @Column(nullable = false, name = "external_id")
  private String externalId;
  
  @Column(name = "external_slug")
  private String externalSlug;

}
