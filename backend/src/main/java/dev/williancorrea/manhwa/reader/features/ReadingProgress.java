package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.time.OffsetDateTime;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "reading_progress",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"user_id", "chapter_id"})
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingProgress implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chapter_id", nullable = false)
  private Chapter chapter;

  @Column(name = "page_number")
  private Integer pageNumber;

  @Column(name = "last_read_at")
  private OffsetDateTime lastReadAt;
}
