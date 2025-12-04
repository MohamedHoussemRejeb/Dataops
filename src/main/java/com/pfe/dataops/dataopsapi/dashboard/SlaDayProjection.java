// src/main/java/com/pfe/dataops/dataopsapi/dashboard/SlaDayProjection.java
package com.pfe.dataops.dataopsapi.dashboard;

import java.time.LocalDate;

public interface SlaDayProjection {
    LocalDate getDay();
    long getOk();
    long getLate();
    long getFailed();
}

