package dev.williancorrea.manhwa.reader.features.comments;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.access.user.User;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.work.Work;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "comment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work work;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chapter_id")
  private Chapter chapter;

  @Column(nullable = false)
  private String content;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;
}
