package dev.williancorrea.manhwa.reader.features.page;

import java.io.Serializable;
import java.util.UUID;
import dev.williancorrea.manhwa.reader.features.chapter.Chapter;
import dev.williancorrea.manhwa.reader.features.storage.FileStorage;
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
@Table(name = "page")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chapter_id", nullable = false)
  private Chapter chapter;

  @Column(name = "page_number", nullable = false)
  private Integer pageNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_file_id", nullable = false)
  private FileStorage imageFile;
}
