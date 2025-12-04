// src/main/java/com/pfe/dataops/dataopsapi/runs/LoggingRunLauncher.java
package com.pfe.dataops.dataopsapi.runs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingRunLauncher implements RunLauncher {

    private final ApplicationEventPublisher events;

    public LoggingRunLauncher(ApplicationEventPublisher events) {
        this.events = events;
    }

    @Override
    public void launchJob(String jobName) {
        // 🔥 On log aussi l'utilisateur qui lance le job (Keycloak)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null ? auth.getName() : "system");

        log.info("[Launcher] User '{}' is launching job '{}'", username, jobName);

        // 🟢 On garde un RunLaunchEvent simple avec uniquement jobName
        events.publishEvent(new RunLaunchEvent(jobName));
    }
}
