// src/main/java/com/pfe/dataops/dataopsapi/dashboard/DashboardController.java
package com.pfe.dataops.dataopsapi.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary getSummary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/timeseries")
    public TimeseriesResponse getTimeseries(@RequestParam(defaultValue = "7") int days) {
        return dashboardService.getTimeseries(days);
    }
}
