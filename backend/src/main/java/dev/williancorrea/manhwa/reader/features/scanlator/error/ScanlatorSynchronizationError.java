package dev.williancorrea.manhwa.reader.features.scanlator.error;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.scanlator.Scanlator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scanlator_synchronization_error")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScanlatorSynchronizationError implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scanlator_id", nullable = false)
  private Scanlator scanlator;
  
  @Column(name = "work_id")
  private String workId;

  @NotNull
  @Size(min = 2, max = 255)
  @Column(nullable = false, name = "external_work_id")
  private String externalWorkId;

  @NotNull
  @Size(min = 2, max = 255)
  @Column(nullable = false, name = "external_work_name")
  private String externalWorkName;

  @Column(name = "error_message")
  private String errorMessage;
  
  @Column(name = "stack_trace")
  private String stackTrace;

}
