// src/main/java/com/pfe/dataops/dataopsapi/dashboard/TimeseriesResponse.java
package com.pfe.dataops.dataopsapi.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TimeseriesResponse {

    private List<RunDayPoint> runs;
    private List<SlaDayPoint> sla;

    @Getter @Setter @Builder
    public static class RunDayPoint {
        private String day;   // "2025-11-26"
        private long total;
        private long failed;
    }

    @Getter @Setter @Builder
    public static class SlaDayPoint {
        private String day;
        private long ok;
        private long late;
        private long failed;
    }
}
