package dev.williancorrea.manhwa.reader.features.scheduling;

import java.io.Serializable;
import java.time.OffsetDateTime;
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
@Table(name = "scheduling")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scheduling implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    @Enumerated(EnumType.STRING)
    private SchedulingName name;

    @Column(length = 255)
    private String description;

    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_unit", nullable = false, length = 20)
    private SchedulingIntervalUnit schedulingIntervalUnit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean monday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean tuesday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean wednesday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean thursday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean friday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean saturday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean sunday = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "last_execution")
    private OffsetDateTime lastExecution;

    @Column(name = "next_execution")
    private OffsetDateTime nextExecution;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
