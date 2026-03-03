package dev.williancorrea.manhwa.reader.features.chapter.notify;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
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
@Table(name = "chapter_notify")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterNotify implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chapter_id", nullable = false)
  private Chapter chapter;

  @Enumerated(EnumType.STRING)
  private ChapterNotifyType status;
}
