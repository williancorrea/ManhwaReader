package dev.williancorrea.manhwa.reader.features.author;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "author")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthorType type;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String biography;

  private String twitter;
  private String pixiv;

  @Column(name = "melon_book")
  private String melonBook;
  @Column(name = "fan_box")
  private String fanBox;
  private String booth;
  private String namicomi;
  @Column(name = "nico_video")
  private String nicoVideo;
  private String skeb;
  private String fantia;
  private String tumblr;
  private String youtube;
  private String weibo;
  private String naver;
  private String website;
}
