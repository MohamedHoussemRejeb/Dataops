// src/main/java/com/pfe/dataops/dataopsapi/dashboard/RunDayProjection.java
package com.pfe.dataops.dataopsapi.dashboard;

import java.time.LocalDate;

public interface RunDayProjection {
    LocalDate getDay();
    long getTotal();
    long getFailed();
}
