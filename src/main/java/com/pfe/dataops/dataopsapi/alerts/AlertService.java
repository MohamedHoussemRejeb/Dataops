package com.pfe.dataops.dataopsapi.alerts;

import com.pfe.dataops.dataopsapi.alerts.dto.AlertCreateRequest;
import com.pfe.dataops.dataopsapi.alerts.dto.AlertDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository repo;

    public AlertService(AlertRepository repo) {
        this.repo = repo;
    }

    public List<AlertDto> listAll() {
        return repo.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(AlertService::toDto)
                .toList();
    }

    @Transactional
    public void acknowledgeMany(List<String> ids) {
        var alerts = repo.findAllById(ids);
        alerts.forEach(a -> a.setAcknowledged(true));
        repo.saveAll(alerts);
    }

    public AlertDto create(AlertCreateRequest req) {
        var a = new Alert();
        a.setSeverity(req.severity());
        a.setSource(req.source());
        a.setRunId(req.runId());
        a.setFlowType(req.flowType());
        a.setMessage(req.message());
        a.setDatasetUrn(req.dataset_urn());
        a.setAcknowledged(false);

        var saved = repo.save(a);
        return toDto(saved);
    }

    // entity -> dto
    public static AlertDto toDto(Alert a) {
        return new AlertDto(
                a.getId(),
                a.getCreatedAt(),
                a.getSeverity(),
                a.getSource(),
                a.getRunId(),
                a.getFlowType(),
                a.getMessage(),
                a.isAcknowledged(),
                a.getDatasetUrn() // devient dataset_urn côté JSON
        );
    }
}
