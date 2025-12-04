// src/main/java/com/pfe/dataops/dataopsapi/dashboard/TopError.java
package com.pfe.dataops.dataopsapi.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopError {
    private String code;
    private String message;
    private long count7d;
}
