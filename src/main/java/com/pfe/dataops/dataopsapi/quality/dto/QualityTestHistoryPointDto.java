// src/main/java/.../quality/dto/QualityTestHistoryPointDto.java
package com.pfe.dataops.dataopsapi.quality.dto;

import java.time.LocalDate;

public record QualityTestHistoryPointDto(
        LocalDate date,
        Double value
) {}
