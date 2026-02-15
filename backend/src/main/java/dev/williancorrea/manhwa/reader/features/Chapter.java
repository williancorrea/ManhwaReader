package dev.williancorrea.manhwa.reader.features;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chapter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal number;

    private String title;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
