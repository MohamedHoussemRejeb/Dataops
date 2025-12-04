//package com.pfe.dataops.dataopsapi.backend.etl.live;

//import com.pfe.dataops.dataopsapi.backend.etl.live.dto.LiveLogDto;
//import com.pfe.dataops.dataopsapi.backend.etl.live.dto.LiveRunDto;
//import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/live")
//@CrossOrigin(origins = "http://localhost:4200")
//public class LiveRunsController {

//private final LiveRunsService service;

//public LiveRunsController(LiveRunsService service) {
//this.service = service;
//}

    /**
     * Liste des runs en cours (status RUNNING)
     * GET /api/live/runs
     */
//@GetMapping("/runs")
//public List<LiveRunDto> liveRuns() {
//return service.getRunning();
//}

    /**
     * Logs d'un run
     * GET /api/live/runs/{id}/logs
     */
//@GetMapping("/runs/{id}/logs")
//public List<LiveLogDto> runLogs(@PathVariable String id) {
//return service.getLogs(id);
//}

    /**
     * Crée un nouveau run RUNNING à la volée
     * POST /api/live/simulate?dryRun=true|false
     */
//@PostMapping("/simulate")
//public LiveRunDto simulate(@RequestParam(defaultValue = "false") boolean dryRun) {
//return service.createSimulatedRun(dryRun);
//}
//}