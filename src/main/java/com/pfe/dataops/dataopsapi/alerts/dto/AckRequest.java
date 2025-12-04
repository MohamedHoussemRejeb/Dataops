// src/main/java/com/pfe/dataops/dataopsapi/alerts/dto/AckRequest.java
package com.pfe.dataops.dataopsapi.alerts.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AckRequest(@NotEmpty List<String> ids) {}
