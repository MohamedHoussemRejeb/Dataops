// src/main/java/com/pfe/dataops/dataopsapi/runs/RunMetadata.java
package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "run_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etl_run_id", nullable = false, unique = true)
    private EtlRun etlRun;

    @Column(name = "row_count_in")
    private Long rowCountIn;

    @Column(name = "row_count_out")
    private Long rowCountOut;

    @Column(name = "error_count")
    private Long errorCount;

    // ✅ the two fields that were missing
    @Column(name = "null_pct")
    private Double nullPct;

    @Column(name = "invalid_pct")
    private Double invalidPct;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "execution_host", length = 255)
    private String executionHost;

    @Column(name = "latency_sec")
    private Long latencySec;

    @Column(name = "cpu_pct")
    private Double cpuPct;

    @Column(name = "ram_pct")
    private Double ramPct;

    @Lob
    @Column(name = "extra_json")
    private String extraJson;
}
