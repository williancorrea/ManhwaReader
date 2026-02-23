package dev.williancorrea.manhwa.reader.features.tag;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "group_tag")
  @Enumerated(EnumType.STRING)
  private TagGroupType group;

  @Column(nullable = false)
  private String name;

  private String alias1;
  private String alias2;
}
