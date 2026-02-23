package dev.williancorrea.manhwa.reader.features.work.link;

import java.io.Serial;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_link")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkLink implements Serializable {

  @Serial
  private static final long serialVersionUID = 5611770789064872793L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id", nullable = false)
  private Work work;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "site_id", nullable = false)
  private Site site;

  @Enumerated(EnumType.STRING)
  private SiteType code;

  @Column(nullable = false)
  private String link;
}
