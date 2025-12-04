// src/main/java/com/pfe/dataops/dataopsapi/notifications/NotificationDto.java
package com.pfe.dataops.dataopsapi.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;           // optionnel si tu persistes plus tard
    private String type;       // RUN, ALERT, QUALITY, USER_ACTION, ...
    private String level;      // INFO, WARNING, ERROR, SUCCESS
    private String title;      // "Run en échec"
    private String message;    // "Le job XXX a échoué..."
    private String targetType; // RUN, ALERT, DATASET, DASHBOARD, ...
    private String targetId;   // ex: etlRun.id
    private String username;   // user ciblé (ownerUsername)
    private Instant createdAt; // Instant.now()
}
