// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceHealth.java
package com.pfe.dataops.dataopsapi.sources;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "source_health")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SourceHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false, unique = true)
    private SoftwareSource source;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceHealthStatus status;

    private Instant lastCheckedAt;
    private Instant lastSuccessAt;

    private Long lastLatencyMs;

    @Column(length = 1000)
    private String lastMessage;
}
