package com.pfe.dataops.dataopsapi.quality;

import com.pfe.dataops.dataopsapi.quality.dto.HeatmapDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualitySeriesDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualitySummaryDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualityTestDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quality")
@CrossOrigin(origins = "*")
public class QualityController {

    private final QualityService service;
    private final QualityTestService testService;  // 👈 nouveau service pour les règles

    public QualityController(QualityService service,
                             QualityTestService testService) {
        this.service = service;
        this.testService = testService;
    }

    @GetMapping("/summary")
    public QualitySummaryDto summary() {
        return service.getSummary();
    }

    @GetMapping("/series")
    public QualitySeriesDto series(
            @RequestParam String metric,
            @RequestParam(defaultValue = "7d") String range
    ) {
        return service.getSeries(metric, range);
    }

    @GetMapping("/heatmap")
    public HeatmapDto heatmap(
            @RequestParam(defaultValue = "7d") String range
    ) {
        return service.getHeatmap(range);
    }
    @GetMapping("/tests/ping")
    public String ping() {
        return "QUALITY TESTS OK";
    }

    // ✅ Nouveau endpoint : règles qualité (Soda/Airflow style)
    @GetMapping("/tests")
    public List<QualityTestDto> tests(@RequestParam("urn") String urn) {
        return testService.buildTestsForDataset(urn);
    }
}
