package com.pfe.dataops.dataopsapi.live;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // si besoin
public class LiveController {

    private final TalendJobService talendJobService;
    private final LiveRunRepository liveRunRepository;
    private final LiveLogRepository liveLogRepository;

    // ✅ Déjà OK : Article
    @PostMapping("/runs/talend/article")
    public LiveRun startArticleRun() {
        return talendJobService.startArticleJob();
    }

    // 🆕 Commande
    @PostMapping("/runs/talend/commande")
    public LiveRun startCommandeRun() {
        return talendJobService.startCommandeJob();
    }

    // 🆕 Annulation
    @PostMapping("/runs/talend/annulation")
    public LiveRun startAnnulationRun() {
        return talendJobService.startAnnulationJob();
    }

    // 🆕 Mouvement stock
    @PostMapping("/runs/talend/mvt-stock")
    public LiveRun startMvtStockRun() {
        return talendJobService.startMouvementStockJob();
    }

    // ---- live runs & logs ----

    @GetMapping("/runs")
    public List<LiveRun> getRunning() {
        return liveRunRepository.findRunningOrRecent(
                LocalDateTime.now().minusMinutes(30)
        );
    }

    @GetMapping("/runs/{id}/logs")
    public List<LiveLog> getLogs(@PathVariable String id) {
        return liveLogRepository.findByRunIdOrderByTsAsc(id);
    }
}
