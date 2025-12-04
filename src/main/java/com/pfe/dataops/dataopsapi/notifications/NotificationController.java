// src/main/java/com/pfe/dataops/dataopsapi/notifications/NotificationController.java
package com.pfe.dataops.dataopsapi.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("/test")
    public void sendTest() {
        // Build a simple test notification using your NotificationDto
        NotificationDto dto = NotificationDto.builder()
                .type("TEST")
                .level("INFO")
                .title("Test notification")
                .message("Ceci est une notification de test depuis /api/notifications/test")
                .targetType("NONE")
                .targetId(null)
                .username(null)          // broadcast only
                .createdAt(Instant.now())
                .build();

        service.sendToAll(dto);
    }
}
