package com.pfe.dataops.dataopsapi.backend.etl.live;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LiveRunsScheduler {

    private final LiveRunsService service;

    public LiveRunsScheduler(LiveRunsService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 2000)
    public void tick() {
        service.tick();
    }
}

